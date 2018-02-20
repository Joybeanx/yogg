package com.joybean.yogg.support;

/**
 * @author joybean
 */
public interface EnumValue {
    int getValue();

    static <T extends EnumValue> T convert(int status, Class<T> type) {
        T[] enumValues = type.getEnumConstants();
        for (T ev : enumValues) {
            if (ev.getValue() == status) {
                return ev;
            }
        }
        return null;
    }
}
