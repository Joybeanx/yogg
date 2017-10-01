package com.joybean.yogg.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.util.concurrent.AtomicLongMap;
import com.joybean.yogg.report.record.RecordStatus;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Statistics data of a task
 *
 * @author jobean
 */
public class TaskStatistics implements Serializable {
    private String taskId;
    /**
     * total number of sending records
     */
    private AtomicInteger total = new AtomicInteger(0);
    /**
     * record counter group by record status
     */
    private AtomicLongMap<RecordStatus> segmentCounterMap = AtomicLongMap.create();

    public TaskStatistics(String taskId) {
        this.taskId = taskId;
    }
    public TaskStatistics() {
    }
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public AtomicInteger getTotal() {
        return total;
    }

    public void setTotal(AtomicInteger total) {
        this.total = total;
    }

    @JsonIgnore
    public AtomicLongMap<RecordStatus> getSegmentCounterMap() {
        return segmentCounterMap;
    }

    @JsonProperty
    public Map<RecordStatus, Long> getCounterMap() {
        return segmentCounterMap.asMap();
    }

    @JsonProperty
    public void setCounterMap(Map<RecordStatus, Long> counterMap) {
        segmentCounterMap.putAll(counterMap);
    }

    public void setSegmentCounterMap(AtomicLongMap<RecordStatus> segmentCounterMap) {
        this.segmentCounterMap = segmentCounterMap;
    }

    @Override
    public String toString() {
        return "TaskStatistics{" +
                "taskId='" + taskId + '\'' +
                ", total=" + total +
                ", segmentCounterMap=" + segmentCounterMap +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskStatistics that = (TaskStatistics) o;

        if (!taskId.equals(that.taskId)) return false;
        if (!total.equals(that.total)) return false;
        return segmentCounterMap.equals(that.segmentCounterMap);

    }

    @Override
    public int hashCode() {
        int result = taskId.hashCode();
        result = 31 * result + total.hashCode();
        result = 31 * result + segmentCounterMap.hashCode();
        return result;
    }
}
