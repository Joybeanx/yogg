package com.joybean.yogg.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.joybean.yogg.datasource.CrawlerDataSource;
import com.joybean.yogg.datasource.DataSourceType;
import com.joybean.yogg.datasource.DatabaseDataSource;
import com.joybean.yogg.datasource.FileDataSource;

/**
 * @author jobean
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "dataSourceType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CrawlerDataSource.class, name = "CRAWLER"),
        @JsonSubTypes.Type(value = FileDataSource.class, name = "FILE"),
        @JsonSubTypes.Type(value = DatabaseDataSource.class, name = "DATABASE")})
public interface IDataSource {
    DataSourceType getDataSourceType();

    @JsonIgnore
    default void setDataSourceType(DataSourceType dataSourceType) {

    }
}
