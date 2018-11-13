package com.createarttechnology.config;

import jutil.StringUtil;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/**
 * Created by lixuhui on 2018/11/13.
 */
class BaseConfig implements Config {

    private Properties properties = new Properties();

    void load(String content) {
        properties = new Properties();
        if (content != null) {
            try {
                properties.load(new StringReader(content));
            } catch (IOException ignored) {
            }
        }
    }

    public String getString(String key, String defaultValue) {
        String result = properties.getProperty(key);
        return result != null ? result : defaultValue;
    }

    public Integer getInt(String key, Integer defaultValue) {
        String result = properties.getProperty(key);
        return StringUtil.convertInt(result, defaultValue);
    }

    public Long getLong(String key, Long defaultValue) {
        String result = properties.getProperty(key);
        return StringUtil.convertLong(result, defaultValue);
    }

    public Float getFloat(String key, Float defaultValue) {
        String result = properties.getProperty(key);
        return StringUtil.convertFloat(result, defaultValue);
    }

    public Double getDouble(String key, Double defaultValue) {
        String result = properties.getProperty(key);
        return StringUtil.convertDouble(result, defaultValue);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        String result = properties.getProperty(key);
        return StringUtil.convertBoolean(result, defaultValue);
    }

}
