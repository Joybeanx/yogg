package com.joybean.yogg.config;

import org.apache.commons.lang3.StringUtils;

/**
 * @author jobean
 */
public class Proxy {
    private Proxy.Type type;
    private String host;
    private String port;

    public Proxy(Proxy.Type type, String host, String port) {
        this.type = type;
        this.host = host;
        this.port = port;
    }

    public Proxy(String type, String host, String port) {
        for (Proxy.Type t : Proxy.Type.values()) {
            if (StringUtils.equalsIgnoreCase(type, t.name())) {
                this.type = t;
                break;
            }
        }
        this.host = host;
        this.port = port;
    }

    public Proxy() {
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public static enum Type {
        DIRECT,
        HTTP,
        SOCKS;
    }
}
