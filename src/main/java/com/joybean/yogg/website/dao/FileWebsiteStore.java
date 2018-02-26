/**
 *
 */
package com.joybean.yogg.website.dao;

import com.joybean.yogg.config.YoggConfig;
import com.joybean.yogg.datasource.DataSourceType;
import com.joybean.yogg.datasource.FileDataSource;
import com.joybean.yogg.support.JsonUtils;
import com.joybean.yogg.support.Pagination;
import com.joybean.yogg.support.YoggException;
import com.joybean.yogg.website.Website;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FileWebsiteStore extends AbstractWebsiteStore {
    @Autowired
    private FileDataSource fileDataSource;
    @Autowired
    private YoggConfig config;

    public FileWebsiteStore(SqlSession sqlSession) {
        super(sqlSession);
    }


    @Override
    public void putWebsite(List<Website> websites) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Website> fetchWebsite(Pagination pagination) {
        return fetchWebsite(pagination, getWebsitePath());
    }

    @Override
    public List<String> fetchWebsiteUrl(Pagination pagination) {
        return fetchWebsiteUrl(pagination, getWebsitePath());
    }

    @Override
    public void shutdown() {
        //no operation
    }



    protected List<Website> fetchWebsite(Pagination pagination, Path filePath) {
        int limit = Integer.MAX_VALUE;
        int offset = 0;
        if (pagination != null) {
            limit = pagination.getLimit();
            offset = pagination.getOffset();
        }
        try {
            return Files.lines(filePath).limit(limit + offset).skip(offset).map(s -> JsonUtils.json2Bean(s, Website.class)).collect(Collectors.toList());
        } catch (IOException e) {
            throw new YoggException(e);
        }
    }

    protected List<String> fetchWebsiteUrl(Pagination pagination, Path filePath) {
        int limit = Integer.MAX_VALUE;
        int offset = 0;
        if (pagination != null) {
            limit = pagination.getLimit();
            offset = pagination.getOffset();
        }
        try {
            return Files.lines(filePath).limit(limit + offset).skip(offset).collect(Collectors.toList());
        } catch (IOException e) {
            throw new YoggException(e);
        }
    }


    private Path getWebsitePath() {
        String filePath = fileDataSource.getFilePath();
        if (StringUtils.isNotBlank(filePath)) {
            return Paths.get(filePath);
        }
        throw new YoggException("Unexpected website file path");
    }


    @Override
    protected DataSourceType getType() {
        return DataSourceType.FILE;
    }
}
