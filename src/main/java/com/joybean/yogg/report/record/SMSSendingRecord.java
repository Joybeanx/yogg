/**
 *
 */
package com.joybean.yogg.report.record;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents the record of attempting to sending sms on a website
 *
 * @author joybean
 */
public class SMSSendingRecord implements Serializable {

    private static final long serialVersionUID = -4921125661623778547L;
    /**
     * The website by which we attempt to send SMS,it can be ip,domain,url
     */
    private String website;
    /**
     * The target page url of the website,such as a url of register page that maybe used for sending SMS
     */
    private String targetPageUrl;
    /**
     * Whether the target page contains captcha
     */
    private boolean hasCaptcha;
    /**
     * The url of sending a SMS on the website,phone number in the url is replaced by "%s"
     */
    private String smsRequestUrl;

    /**
     * The exception during execution
     */
    private String exception;
    /**
     * The result status of sending SMS by website
     */
    private RecordStatus status = RecordStatus.UNKNOWN;
    /**
     * The id of the task current record belongs to
     */
    private String taskId;
    /**
     * The create time of current record
     */
    private Date createTime;

    public SMSSendingRecord() {

    }

    public SMSSendingRecord(String taskId, String website) {
        this.taskId = taskId;
        this.website = website;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTargetPageUrl() {
        return targetPageUrl;
    }

    public void setTargetPageUrl(String targetPageUrl) {
        this.targetPageUrl = targetPageUrl;
    }

    public String getSmsRequestUrl() {
        return smsRequestUrl;
    }

    public void setSmsRequestUrl(String smsRequestUrl) {
        this.smsRequestUrl = smsRequestUrl;
    }

    public boolean isHasCaptcha() {
        return hasCaptcha;
    }

    public void setHasCaptcha(boolean hasCaptcha) {
        this.hasCaptcha = hasCaptcha;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Date getCreateTime() {
        //findbugs:May expose internal representation by returning reference to mutable object
        if (createTime != null) {
            return new Date(createTime.getTime());
        }
        return null;
    }

    public void setCreateTime(Date createTime) {
        if (createTime == null) {
            this.createTime = null;
        } else {
            this.createTime = new Date(createTime.getTime());
        }
    }

    @Override
    public String toString() {
        return "SMSSendingRecord{" +
                "website='" + website + '\'' +
                ", targetPageUrl='" + targetPageUrl + '\'' +
                ", hasCaptcha=" + hasCaptcha +
                ", smsRequestUrl='" + smsRequestUrl + '\'' +
                ", exception='" + exception + '\'' +
                ", status=" + status +
                ", taskId='" + taskId + '\'' +
                ", createTime=" + createTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SMSSendingRecord that = (SMSSendingRecord) o;

        if (hasCaptcha != that.hasCaptcha) return false;
        if (!website.equals(that.website)) return false;
        if (targetPageUrl != null ? !targetPageUrl.equals(that.targetPageUrl) : that.targetPageUrl != null)
            return false;
        if (smsRequestUrl != null ? !smsRequestUrl.equals(that.smsRequestUrl) : that.smsRequestUrl != null)
            return false;
        if (exception != null ? !exception.equals(that.exception) : that.exception != null) return false;
        if (status != that.status) return false;
        if (!taskId.equals(that.taskId)) return false;
        return createTime != null ? createTime.equals(that.createTime) : that.createTime == null;

    }

    @Override
    public int hashCode() {
        int result = website.hashCode();
        result = 31 * result + (targetPageUrl != null ? targetPageUrl.hashCode() : 0);
        result = 31 * result + (hasCaptcha ? 1 : 0);
        result = 31 * result + (smsRequestUrl != null ? smsRequestUrl.hashCode() : 0);
        result = 31 * result + (exception != null ? exception.hashCode() : 0);
        result = 31 * result + status.hashCode();
        result = 31 * result + taskId.hashCode();
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        return result;
    }
}
