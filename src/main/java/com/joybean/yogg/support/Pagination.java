/**
 *
 */
package com.joybean.yogg.support;

/**
 * @author joybean
 */
public class Pagination {

    public final static int DEFAULT_PAGE_SIZE = 20;
    private int offset;
    private int limit;

    public Pagination(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public Pagination(int limit) {
        this(0, limit);
    }

    public Pagination() {
        this(0, DEFAULT_PAGE_SIZE);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "Pagination [offset=" + offset + ", limit=" + limit + "]";
    }

}
