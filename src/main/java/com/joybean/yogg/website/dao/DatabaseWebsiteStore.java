/**
 *
 */
package com.joybean.yogg.website.dao;

import com.joybean.yogg.datasource.DataSourceType;
import com.joybean.yogg.support.Pagination;
import com.joybean.yogg.website.Website;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.google.common.collect.ImmutableMap.of;

@Repository
public class DatabaseWebsiteStore extends AbstractWebsiteStore {
    private final SqlSession sqlSession;

    public DatabaseWebsiteStore(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public void insertWebsite(List<Website> websites) {
        sqlSession.insert("insertWebsite", of("websites", websites));
    }

    @Override
    public void replaceWebsite(List<Website> websites) {
        sqlSession.insert("replaceWebsite", of("websites", websites));
    }


    @Override
    public List<Website> fetchWebsite(Pagination pagination) {
        return sqlSession.selectList("queryWebsite", of("pagination", pagination));
    }

    @Override
    public List<String> fetchWebsiteUrl(Pagination pagination) {
        return sqlSession.selectList("queryWebsiteUrl", of("pagination", pagination));
    }

    @Override
    public long countWebsite() {
        return sqlSession.selectOne("countWebsite");
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


    @Override
    public void shutdown() {
        //no operation
    }

    @Override
    protected DataSourceType getType() {
        return DataSourceType.DATABASE;
    }
}
