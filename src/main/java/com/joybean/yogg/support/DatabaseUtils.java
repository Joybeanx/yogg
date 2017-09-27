package com.joybean.yogg.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseUtils {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(DatabaseUtils.class);

    public static boolean testConnection(String driverClassName, String url, String username, String password) {
        Connection conn = null;
        try {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            LOGGER.warn("Failed to connect database by driverClassName[{}],url[{}],username[{}],password[**************]", e);
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
        return true;
    }
}
