package com.joybean.yogg.datasource;

import com.joybean.yogg.config.IDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * File data source that supplies website urls
 *
 * @author jobean
 */
@Component
@ConfigurationProperties(prefix = "file-datasource")
public class FileDataSource implements IDataSource {
    /**
     * The path of file that store website urls,new line for each url
     */
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.FILE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileDataSource that = (FileDataSource) o;

        return getFilePath().equals(that.getFilePath());

    }

    @Override
    public int hashCode() {
        return getFilePath().hashCode();
    }
}
