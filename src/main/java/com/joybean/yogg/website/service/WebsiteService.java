/**
 *
 */
package com.joybean.yogg.website.service;

import com.joybean.yogg.support.Pagination;
import com.joybean.yogg.website.Website;

import java.util.List;

/**
 * @author joybean
 */
public interface WebsiteService {

    void insertWebsite(Website... websites);

    void insertWebsite(List<Website> websites);

    void insertWebsite(String[] urls);

    void insertWebsite_(List<String> urls);

    void replaceWebsite(Website... websites);

    void replaceWebsite(List<Website> websites);

    List<Website> fetchWebsite(Pagination pagination);

    List<String> fetchWebsiteUrls(Pagination pagination);

    long countWebsite();

    void insertKeyWebsite(Website... websites);

    void insertKeyWebsite(List<Website> websites);

    void insertKeyWebsite(String[] urls);

    void insertKeyWebsite_(List<String> urls);

    void replaceKeyWebsite(Website... websites);

    void replaceKeyWebsite(List<Website> websites);

    List<Website> fetchKeyWebsite(Pagination pagination);

    List<String> fetchKeyWebsiteUrls(Pagination pagination);

    long countKeyWebsite();

    void shutdown();

}
