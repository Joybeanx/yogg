/**
 *
 */
package com.joybean.yogg.task.executor;


import com.joybean.yogg.config.YoggConfig;
import com.joybean.yogg.report.TaskReport;
import com.joybean.yogg.report.TaskStatistics;
import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.report.record.SMSSendingRecord;
import com.joybean.yogg.report.service.ReportService;
import com.joybean.yogg.simulator.HtmlUnitSMSSendingSimulator;
import com.joybean.yogg.support.JsonUtils;
import com.joybean.yogg.support.Pagination;
import com.joybean.yogg.support.ThreadUtils;
import com.joybean.yogg.task.Task;
import com.joybean.yogg.task.TaskStatus;
import com.joybean.yogg.website.Website;
import com.joybean.yogg.website.service.WebsiteService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author joybean
 */

public abstract class AbstractTaskExecutor implements TaskExecutor {
    private final Logger LOGGER = LoggerFactory
            .getLogger(this.getClass());
    private final static List<TaskExecutor> taskExecutorList = new ArrayList<>();
    @Autowired
    private YoggConfig config;
    @Autowired
    private ReportService reportService;
    @Autowired
    protected WebsiteService websiteService;
    @Autowired
    @Qualifier("htmlUnitSMSSendingSimulator")
    private HtmlUnitSMSSendingSimulator sendingSimulator;
    private ThreadPoolExecutor threadPool;

    @PostConstruct
    private void init() {
        if (!(this instanceof TaskExecutorImpl)) {
            threadPool = initThreadPool();
            taskExecutorList.add(this);
        }
    }

    private ThreadPoolExecutor initThreadPool() {
        int threads = config.getThreads();
        return (ThreadPoolExecutor) ThreadUtils.initFixedThreadPool(
                "TaskExecutorThread-%d", threads);
    }

    protected TaskReport doExecute(Task task) {
        String taskId = task.getTaskId();
        LOGGER.info("Start to execute task [{}]", taskId);
        LOGGER.info("Prepare to send SMS to {}", task.getTargetPhoneNumbers());
        TaskReport taskReport = null;
        try {
            task.setTaskStatus(TaskStatus.RUNNING);
            long startTime = System.currentTimeMillis();
            taskReport = new TaskReport(taskId);
            taskReport.setStartTime(new Date(startTime));
            Function<Pagination, List<String>> urlProducer = getUrlProducer();
            List<String> targetPhoneNumbers = task.getTargetPhoneNumbers();
            int urlOffset = 0;
            TaskStatistics statistics = new TaskStatistics(taskId);
            taskReport.setStatistics(statistics);
            List<String> urls;
            List<CompletableFuture<SMSSendingRecord>> futures = new ArrayList<>();
            while (!interrupted()) {
                try {
                    int batchSize = config.getThreads() << 1;
                    //Keep submitting website to simulator until the queue size of thread pool has reached a threshold
                    if (threadPool.getQueue().size() < batchSize) {
                        //fetch urls from producer by page
                        if (CollectionUtils.isEmpty(urls = urlProducer.apply(new Pagination(urlOffset, batchSize)))) {
                            LOGGER.info("Urls has been used up");
                            break;
                        }
                        LOGGER.info("Fetched urls {}", urls);
                        //execute asynchronously and handle result
                        executeAsync(targetPhoneNumbers, urls, statistics, futures);
                        LOGGER.info("[time cost={},completed={},active={},queued={}] ", currentTimeCost(startTime), threadPool.getCompletedTaskCount(), threadPool.getActiveCount(), threadPool.getQueue().size());
                        updateTaskReport(taskReport);
                        urlOffset += batchSize;
                    }
                } catch (Throwable e) {
                    LOGGER.error("Exception occurred when execute task [{}]\r\n", taskId, e);
                    task.setTaskStatus(TaskStatus.FAILED);
                    taskReport.setException(e);
                    break;
                }
            }

            if (interrupted()) {
                cancelIncomplete(futures);
                LOGGER.info("Task [{}] has been stopped", taskId);
                task.setTaskStatus(TaskStatus.CANCELLED);
            } else {
                try {
                    waitToComplete(futures);
                } catch (Throwable e) {
                    LOGGER.error("Error during waiting to complete", e);
                    task.setTaskStatus(TaskStatus.FAILED);
                    taskReport.setException(e);
                }
                if (TaskStatus.FAILED != task.getTaskStatus()) {
                    task.setTaskStatus(TaskStatus.SUCCEEDED);
                    taskReport.setComplete(true);
                }
            }
        } finally {
            taskReport.setFinishTime(new Date());
            LOGGER.info("Report of task [{}]:\r\n{}", taskId, JsonUtils.bean2PrettyJson(taskReport));
            updateTaskReport(taskReport);
        }
        return taskReport;
    }


    private void executeAsync(List<String> targetPhoneNumbers, List<String> websites, TaskStatistics statistics, List<CompletableFuture<SMSSendingRecord>> futures) {
        String taskId = statistics.getTaskId();
        List<CompletableFuture<SMSSendingRecord>> newFutures = websites.stream().parallel().filter(e -> !interrupted())
                .map(d -> CompletableFuture.supplyAsync(() -> {
                            LOGGER.info("Trying to send SMS by {}...", d);
                            SMSSendingRecord record = sendingSimulator.trySend(targetPhoneNumbers, d, taskId);
                            return record;
                        }, threadPool).whenCompleteAsync((r, a) -> onComplete(statistics, r))
                ).collect(Collectors.toList());
        storeAndRemoveDone(futures);
        futures.addAll(newFutures);
    }

    private void onComplete(TaskStatistics statistics, SMSSendingRecord record) {
        if (record != null) {
            recordLog(record);
            doStatistics(statistics, record);
        }
    }


    private void waitToComplete(List<CompletableFuture<SMSSendingRecord>> futures) {
        List<SMSSendingRecord> records = futures.stream().map(f -> f.join()).collect(Collectors.toList());
        storeDone(records);
        collectAndStoreKeyWebsite(records);
    }

    private void cancelIncomplete(List<CompletableFuture<SMSSendingRecord>> futures) {
        threadPool.getQueue().clear();
        futures.forEach(f -> f.complete(null));
    }

    private void storeAndRemoveDone(List<CompletableFuture<SMSSendingRecord>> futures) {
        List<CompletableFuture<SMSSendingRecord>> doneFutures = new ArrayList<>();
        futures.removeIf(f -> {
            if (f.isDone()) {
                doneFutures.add(f);
                return true;
            }
            return false;
        });
        List<SMSSendingRecord> availableRecords = doneFutures.stream().map(f -> f.join()).collect(Collectors.toList());
        storeDone(availableRecords);
        //TODO should not always store key website
        collectAndStoreKeyWebsite(availableRecords);
    }

    private void updateTaskReport(TaskReport taskReport) {
        try {
            reportService.saveTaskReport(taskReport);
        } catch (Exception e) {
            //ignore ClosedByInterruptException when task is interrupted
        }
    }


    /**
     * Store done records
     *
     * @param records the records to save
     */
    private void storeDone(List<SMSSendingRecord> records) {
        reportService.replaceSMSSendingRecord(records);
    }

    /**
     * Collect website from successful records and store them as key websites
     *
     * @param records the complete records
     */
    private void collectAndStoreKeyWebsite(List<SMSSendingRecord> records) {
        List<Website> successWebsites = records.stream().filter(r -> RecordStatus.SUCCESS == r.getStatus()).map(r -> new Website(r.getWebsite())).collect(Collectors.toList());
        websiteService.replaceKeyWebsite(successWebsites);
    }

    private void doStatistics(TaskStatistics statistics, SMSSendingRecord... records) {
        statistics.getTotal().addAndGet(records.length);
        Stream.of(records).forEach(r -> statistics.getSegmentCounterMap().incrementAndGet(r.getStatus()));
    }


    private Function<Pagination, List<String>> getUrlProducer() {
        if (isMainTaskComplete()) {
            LOGGER.info("Intend to execute task based on key websites");
            return t -> websiteService.fetchKeyWebsiteUrls(t);
        }
        LOGGER.info("Intend to execute task based on websites store");
        return t -> websiteService.fetchWebsiteUrls(t);
    }


    private boolean isMainTaskComplete() {
        return reportService.isTaskReportComplete(Task.TASK_ID_MAIN);
    }

    private void recordLog(SMSSendingRecord record) {
        if (record == null) {
            return;
        }
        String website = record.getWebsite();
        RecordStatus status = record.getStatus();
        String exception = record.getException();
        String log;
        if (RecordStatus.SUCCESS == status) {
            log = String.format("Succeeded in sending SMS by %s", record.getSmsRequestUrl());
        } else {
            log = String.format("Unable to send SMS by %s,cause:%s", website, status);
            if (StringUtils.isNotEmpty(exception)) {
                log += ",exception:" + exception;
            }
        }
        LOGGER.info(log);
    }

    private boolean interrupted() {
        return Thread.currentThread().isInterrupted();
    }

    /**
     * Build a readable time cost
     *
     * @param startTime start time of the task
     * @return formatted time cost,for example:10 minutes,3 hours
     */
    private String currentTimeCost(long startTime) {
        StringBuilder formattedTime = new StringBuilder();
        long duration = (System.currentTimeMillis() - startTime) / 1000;
        int day = (int) TimeUnit.SECONDS.toDays(duration);
        long hours = TimeUnit.SECONDS.toHours(duration) - (day * 24);
        long minutes = TimeUnit.SECONDS.toMinutes(duration) - (TimeUnit.SECONDS.toHours(duration) * 60);
        if (day != 0) {
            formattedTime.append(day).append(" days,");
        }
        if (hours != 0) {
            formattedTime.append(hours).append(" hours,");
        }
        if (minutes != 0) {
            formattedTime.append(minutes).append(" minutes,");
        }
        if (formattedTime.length() > 0) {
            formattedTime.deleteCharAt(formattedTime.length() - 1);
        }
        return formattedTime.toString();

    }

    public List<TaskExecutor> getTaskExecutorList() {
        return taskExecutorList;
    }

    public ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }
}
