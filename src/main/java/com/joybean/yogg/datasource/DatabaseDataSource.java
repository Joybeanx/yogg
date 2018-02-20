package com.joybean.yogg.datasource;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.joybean.yogg.config.IDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Database data source that stores websites and sending records
 * @author joybean
 */
@Component
@ConfigurationProperties(prefix = "database-datasource")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class DatabaseDataSource extends BasicDataSource implements IDataSource {
    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.DATABASE;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatabaseDataSource that = (DatabaseDataSource) o;
        if (!getDriverClassName().equals(that.getDriverClassName())) return false;
        if (!getUrl().equals(that.getUrl())) return false;
        if (!getUsername().equals(that.getUsername())) return false;
        return getPassword().equals(that.getPassword());

    }
    @Override
    public int hashCode() {
        int result = getDriverClassName().hashCode();
        result = 31 * result + getUrl().hashCode();
        result = 31 * result + getUsername().hashCode();
        result = 31 * result + getPassword().hashCode();
        return result;
    }
}
