package com.joybean.yogg.website.dao;

import com.joybean.yogg.datasource.DataSourceType;
import com.joybean.yogg.support.Pagination;
import com.joybean.yogg.website.Website;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;

/**
 * @author joybean
 */
public abstract class AbstractWebsiteStore implements WebsiteStore {
    @Autowired
    private WebsiteStoreFactory websiteStoreFactory;
    protected final SqlSession sqlSession;

    public AbstractWebsiteStore(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @PostConstruct
    protected void register() {
        DataSourceType type = getType();
        websiteStoreFactory.register(type, this);
    }
    @Override
    public void insertKeyWebsite(List<Website> websites) {
        sqlSession.insert("insertKeyWebsite", of("websites", websites));
    }

    @Override
    public void replaceKeyWebsite(List<Website> websites) {
        sqlSession.insert("replaceKeyWebsite", of("websites", websites));
    }


    @Override
    public List<Website> fetchKeyWebsite(Pagination pagination) {
        return sqlSession.selectList("queryKeyWebsite", of("pagination", pagination));
    }

    @Override
    public List<String> fetchKeyWebsiteUrl(Pagination pagination) {
        return sqlSession.selectList("queryKeyWebsiteUrl", of("pagination", pagination));
    }

    @Override
    public long countKeyWebsite() {
        return sqlSession.selectOne("countKeyWebsite");
    }

    protected abstract DataSourceType getType();
}
