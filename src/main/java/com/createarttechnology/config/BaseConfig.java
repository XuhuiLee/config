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

    @Override
    public String getString(String key, String defaultValue) {
        String result = properties.getProperty(key);
        return result != null ? result : defaultValue;
    }

    @Override
    public Integer getInt(String key, Integer defaultValue) {
        String result = properties.getProperty(key);
        return StringUtil.convertInt(result, defaultValue);
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        String result = properties.getProperty(key);
        return StringUtil.convertLong(result, defaultValue);
    }

    @Override
    public Float getFloat(String key, Float defaultValue) {
        String result = properties.getProperty(key);
        return StringUtil.convertFloat(result, defaultValue);
    }

    @Override
    public Double getDouble(String key, Double defaultValue) {
        String result = properties.getProperty(key);
        return StringUtil.convertDouble(result, defaultValue);
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        String result = properties.getProperty(key);
        return StringUtil.convertBoolean(result, defaultValue);
    }

}
