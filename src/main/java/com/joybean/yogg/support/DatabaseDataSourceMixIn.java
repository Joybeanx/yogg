package com.joybean.yogg.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.joybean.yogg.datasource.DatabaseDataSource;

/**
 * DatabaseDataSource mixin for jackson converter
 */
public abstract class DatabaseDataSourceMixIn {
    @JsonProperty
    String driverClassName;
    @JsonProperty
    String url;
    @JsonProperty
    String username;
    @JsonProperty
    volatile String password;
    @JsonProperty
    int maxTotal;
    @JsonProperty
    int minIdle;
    @JsonProperty
    int initialSize;
    @JsonProperty
    long maxWaitMillis;
    @JsonProperty
    long timeBetweenEvictionRunsMillis;

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatabaseDataSourceMixIn that = (DatabaseDataSourceMixIn) o;

        if (getMaxTotal() != that.getMaxTotal()) return false;
        if (getMinIdle() != that.getMinIdle()) return false;
        if (getInitialSize() != that.getInitialSize()) return false;
        if (getMaxWaitMillis() != that.getMaxWaitMillis()) return false;
        if (getTimeBetweenEvictionRunsMillis() != that.getTimeBetweenEvictionRunsMillis()) return false;
        if (!getDriverClassName().equals(that.getDriverClassName())) return false;
        if (!getUrl().equals(that.getUrl())) return false;
        if (!getUsername().equals(that.getUsername())) return false;
        if (!getPassword().equals(that.getPassword())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = getDriverClassName().hashCode();
        result = 31 * result + getUrl().hashCode();
        result = 31 * result + getUsername().hashCode();
        result = 31 * result + getPassword().hashCode();
        result = 31 * result + getMaxTotal();
        result = 31 * result + getMinIdle();
        result = 31 * result + getInitialSize();
        result = 31 * result + (int) (getMaxWaitMillis() ^ (getMaxWaitMillis() >>> 32));
        result = 31 * result + (int) (getTimeBetweenEvictionRunsMillis() ^ (getTimeBetweenEvictionRunsMillis() >>> 32));
        return result;
    }
}
