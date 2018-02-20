package com.joybean.yogg.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joybean.yogg.datasource.DatabaseDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

/**
 * @author joybean
 */
public class JsonUtils {

    private final static Logger LOGGER = LoggerFactory
            .getLogger(JsonUtils.class);
    private final static ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.addMixIn(DatabaseDataSource.class, DatabaseDataSourceMixIn.class);
        MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public static <T> T json2Bean(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            LOGGER.error("Failed to convert json {} to bean of class {}", json, clazz.getSimpleName(), e);
            throw new YoggException(e);
        }
    }

    public static <T> String bean2PrettyJson(T bean) {
        if (bean == null) {
            return null;
        }
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(bean);
        } catch (IOException e) {
            LOGGER.error("Failed to convert bean {} to pretty json.", bean, e);
            throw new YoggException(e);
        }
    }

    public static <T> String bean2Json(T bean) {
        if (bean == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(bean);
        } catch (IOException e) {
            LOGGER.error("Failed to convert bean {} to json.", bean, e);
            throw new YoggException(e);
        }
    }


    public static <T> void write(T t, String filePath) {
        Path path = Paths.get(filePath);
        if (path != null) {
            String json = JsonUtils.bean2PrettyJson(t);
            try {
                Files.write(path, json.getBytes(Charset.defaultCharset()));
            } catch (IOException e) {
                throw new YoggException("Failed to write %s to %s", t, filePath);
            }
        }
    }

    public static <T> T read(Class<T> clazz, String filePath) {
        Path path = validate(filePath);
        if (path != null) {
            String json;
            try {
                json = new String(Files.readAllBytes(path), Charset.defaultCharset());
            } catch (IOException e) {
                throw new YoggException("Failed to read json file %s", filePath);
            }
            return JsonUtils.json2Bean(json, clazz);
        }
        return null;
    }

    private static Path validate(String filePath) {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            return path;
        }
        LOGGER.warn("File path {} doesn't not exist", filePath);
        return null;
    }

}
