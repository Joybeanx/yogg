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


    public DatabaseWebsiteStore(SqlSession sqlSession) {
        super(sqlSession);
    }

    @Override
    public void putWebsite(List<Website> websites) {
        throw new UnsupportedOperationException();
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
    public void shutdown() {
        //no operation
    }

    @Override
    protected DataSourceType getType() {
        return DataSourceType.DATABASE;
    }
}
