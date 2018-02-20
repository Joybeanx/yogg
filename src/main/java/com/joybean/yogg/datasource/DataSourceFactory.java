package com.joybean.yogg.datasource;

import com.joybean.yogg.config.IDataSource;
import com.joybean.yogg.support.ContextHolder;
import com.joybean.yogg.support.YoggException;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author joybean
 */
public class DataSourceFactory {

    public static IDataSource getDataSource(String dataSourceType) {
        Map<String, IDataSource> dataSourceMap = ContextHolder.getSpringContext().getBeansOfType(IDataSource.class);
        for (IDataSource dataSource : dataSourceMap.values()) {
            if (StringUtils.equalsIgnoreCase(dataSourceType, dataSource.getDataSourceType().name())) {
                return dataSource;
            }
        }
        throw new YoggException("No matched data source found by %s", dataSourceType);
    }

    public static IDataSource getDataSource(DataSourceType dataSourceType) {
        Map<String, IDataSource> dataSourceMap = ContextHolder.getSpringContext().getBeansOfType(IDataSource.class);
        for (IDataSource dataSource : dataSourceMap.values()) {
            if (dataSourceType.equals(dataSource.getDataSourceType())) {
                return dataSource;
            }
        }
        throw new YoggException("No matched data source found by %s", dataSourceType);
    }

    public static <T extends IDataSource> T getDataSource(Class<T> clazz) {
        T dataSource = ContextHolder.getSpringContext().getBean(clazz);
        if (dataSource == null) {
            throw new YoggException("No matched data source found by %s", clazz);
        }
        return dataSource;
    }
}
