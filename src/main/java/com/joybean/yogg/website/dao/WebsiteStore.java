/**
 *
 */
package com.joybean.yogg.website.dao;

import com.joybean.yogg.website.Website;
import com.joybean.yogg.support.Pagination;

import java.util.List;

public interface WebsiteStore {
    void insertWebsite(List<Website> websites);

    void replaceWebsite(List<Website> websites);

    List<Website> fetchWebsite(Pagination pagination);

    List<String> fetchWebsiteUrl(Pagination pagination);

    long countWebsite();

    void insertKeyWebsite(List<Website> websites);

    void replaceKeyWebsite(List<Website> websites);

    List<Website> fetchKeyWebsite(Pagination pagination);

    List<String> fetchKeyWebsiteUrl(Pagination pagination);

    long countKeyWebsite();

    void shutdown();
}
