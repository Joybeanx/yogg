package com.joybean.yogg.datasource;

import com.joybean.yogg.support.EnumValue;

/**
 * @author joybean
 */
public enum DataSourceType implements EnumValue {
    CRAWLER(0), FILE(1), DATABASE(2);
    private int value;

    DataSourceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
