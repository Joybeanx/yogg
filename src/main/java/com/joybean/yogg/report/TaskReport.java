package com.joybean.yogg.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.joybean.yogg.report.record.SMSSendingRecord;
import com.joybean.yogg.support.JsonUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * A task report is generated after task is complete or failed or cancelled
 *
 * @author joybean
 */
public class TaskReport implements Serializable {
    private static final long serialVersionUID = -1311918924778536152L;
    private String taskId;
    /**
     * sending records of the task
     */
    @JsonIgnore
    private List<SMSSendingRecord> records;
    /**
     * task statistics
     */
    private TaskStatistics statistics = new TaskStatistics();
    /**
     * task exception
     */
    private Throwable exception;
    /**
     * time when task started
     */
    private Date startTime;
    /**
     * time when task finished
     */
    private Date finishTime;
    /**
     * Whether task completes consuming website in the data source or not.
     * Main task will consume all the websites,but as for other tasks,they only consume once successful ones
     */
    private boolean isComplete;

    public TaskReport(String taskId) {
        this.taskId = taskId;
    }

    public TaskReport() {

    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public List<SMSSendingRecord> getRecords() {
        return records;
    }

    public void setRecords(List<SMSSendingRecord> records) {
        this.records = records;
    }

    public TaskStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(TaskStatistics statistics) {
        this.statistics = statistics;
    }

    @JsonIgnore
    public void setException(Throwable exception) {
        this.exception = exception;
    }

    @JsonIgnore
    public Throwable getException() {
        return exception;
    }

    public String getError() {
        if (exception != null) {
            return exception.toString();
        }
        return null;
    }

    public void setError(String error) {

    }


    public Date getStartTime() {
        //findbugs:May expose internal representation by returning reference to mutable object
        if (startTime != null) {
            return new Date(startTime.getTime());
        }
        return null;
    }

    public void setStartTime(Date startTime) {
        if (startTime == null) {
            this.startTime = null;
        } else {
            this.startTime = new Date(startTime.getTime());
        }
    }

    public Date getFinishTime() {
        //findbugs:May expose internal representation by returning reference to mutable object
        if (finishTime != null) {
            return new Date(finishTime.getTime());
        }
        return null;
    }

    public void setFinishTime(Date finishTime) {
        if (finishTime == null) {
            this.finishTime = null;
        } else {
            this.finishTime = new Date(finishTime.getTime());
        }
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getContent() {
        String content = JsonUtils.bean2PrettyJson(this);
        return content;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("taskId", taskId)
                .append("records", records)
                .append("exception", exception)
                .append("statistics", statistics)
                .append("startTime", startTime)
                .append("finishTime", finishTime)
                .append("isComplete", isComplete)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskReport that = (TaskReport) o;

        if (isComplete() != that.isComplete()) return false;
        if (!getTaskId().equals(that.getTaskId())) return false;
        if (getRecords() != null ? !getRecords().equals(that.getRecords()) : that.getRecords() != null) return false;
        if (getStatistics() != null ? !getStatistics().equals(that.getStatistics()) : that.getStatistics() != null)
            return false;
        if (getException() != null ? !getException().equals(that.getException()) : that.getException() != null)
            return false;
        if (!getStartTime().equals(that.getStartTime()))
            return false;
        return getFinishTime().equals(that.getFinishTime());

    }

    @Override
    public int hashCode() {
        int result = getTaskId().hashCode();
        result = 31 * result + (getRecords() != null ? getRecords().hashCode() : 0);
        result = 31 * result + (getStatistics() != null ? getStatistics().hashCode() : 0);
        result = 31 * result + (getException() != null ? getException().hashCode() : 0);
        result = 31 * result + getStartTime().hashCode();
        result = 31 * result + getFinishTime().hashCode();
        result = 31 * result + (isComplete() ? 1 : 0);
        return result;
    }
}
