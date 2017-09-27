/**
 *
 */
package com.joybean.yogg.website.dao;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.joybean.yogg.config.YoggConfig;
import com.joybean.yogg.datasource.DataSourceType;
import com.joybean.yogg.datasource.FileDataSource;
import com.joybean.yogg.support.JsonUtils;
import com.joybean.yogg.support.Pagination;
import com.joybean.yogg.support.YoggException;
import com.joybean.yogg.website.Website;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FileWebsiteStore extends AbstractWebsiteStore {
    @Autowired
    private FileDataSource fileDataSource;
    @Autowired
    private YoggConfig config;
    private Path keyWebsiteFilePath;
    private final static int KEY_WEBSITE_EXPECTED_INSERTION = 5000;
    /**
     * A bloom filter try to ensure the uniqueness of key website in file
     */
    private final static BloomFilter<String> keyWebsiteBloomFilter = BloomFilter.create(
            Funnels.unencodedCharsFunnel(), KEY_WEBSITE_EXPECTED_INSERTION);

    @PostConstruct
    public void initKeyWebsiteFile() {
        keyWebsiteFilePath = getKeyWebsitePath();
        Path keyWebsiteDir = keyWebsiteFilePath.getParent();
        try {
            if (keyWebsiteDir != null && Files.notExists(keyWebsiteDir)) {
                Files.createDirectory(keyWebsiteDir);
            }
            if (Files.notExists(keyWebsiteFilePath)) {
                Files.createFile(keyWebsiteFilePath);
            }
        } catch (IOException e) {
            throw new YoggException("Failed to create key website file", e);
        }
        //Initialize bloom filter by existing key websites in file
        List<String> keyWebsiteUrls = fetchKeyWebsiteUrl(null);
        keyWebsiteUrls.forEach(u -> keyWebsiteBloomFilter.put(u));
    }


    @Override
    public void insertWebsite(List<Website> websites) {
        insertWebsite(websites, getWebsitePath(), null);
    }


    @Override
    public void replaceWebsite(List<Website> websites) {
        replaceWebsite(websites, getWebsitePath());
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
    public long countWebsite() {
        return countWebsite(getWebsitePath());
    }

    @Override
    public void insertKeyWebsite(List<Website> websites) {
        insertWebsite(websites, keyWebsiteFilePath, keyWebsiteBloomFilter);
    }

    @Override
    public void replaceKeyWebsite(List<Website> websites) {
        replaceWebsite(websites, keyWebsiteFilePath);
    }

    @Override
    public List<Website> fetchKeyWebsite(Pagination pagination) {
        return fetchWebsite(pagination, keyWebsiteFilePath);
    }

    @Override
    public List<String> fetchKeyWebsiteUrl(Pagination pagination) {
        return fetchWebsite(pagination, keyWebsiteFilePath).stream().map(w -> w.getUrl()).collect(Collectors.toList());
    }

    @Override
    public long countKeyWebsite() {
        return countWebsite(keyWebsiteFilePath);
    }

    @Override
    public void shutdown() {
        //no operation
    }


    protected void insertWebsite(List<Website> websites, Path filePath, BloomFilter<String> bloomFilter) {
        websites.stream().filter(w -> bloomFilter == null || (bloomFilter != null && !bloomFilter.mightContain(w.getUrl()))).forEach(w -> {
            try {
                Files.write(filePath, (JsonUtils.bean2Json(w) + StringUtils.LF).getBytes(), StandardOpenOption.APPEND);
                if (bloomFilter != null) {
                    bloomFilter.put(w.getUrl());
                }
            } catch (IOException e) {
                throw new YoggException(e);
            }
        });
    }


    //TODO to be implemented
    protected void replaceWebsite(List<Website> websites, Path filePath) {
        insertWebsite(websites, filePath, null);
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

    protected long countWebsite(Path filePath) {
        try {
            return Files.lines(filePath).count();
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

    private Path getKeyWebsitePath() {
        String filePath = config.getKeyWebsiteFileName();
        if (StringUtils.isNotBlank(filePath)) {
            return Paths.get(filePath);
        }
        throw new YoggException("Unexpected key website file path");
    }

    @Override
    protected DataSourceType getType() {
        return DataSourceType.FILE;
    }
}
