package com.joybean.yogg.support;

import com.joybean.yogg.datasource.DataSourceType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author joybean
 */
public class InstanceFactory<K, V> {
    private final Map<K, V> instances = new HashMap<>();

    public void register(K type, V instance) {
        if (type != null) {
            instances.put(type, instance);
        }
    }

    public V getInstance(DataSourceType type) {
        return instances.get(type);
    }

}
