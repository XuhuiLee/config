package com.createarttechnology.config;

import com.createarttechnology.jutil.StringUtil;

/**
 * 抽象Config类，抽离类型转换逻辑
 * Created by lixuhui on 2019/7/9.
 */
abstract class AbstractConfig implements Config {
    
    abstract String getValue(String key);

    @Override
    public String getString(String key, String defaultValue) {
        String value = getValue(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public Integer getInt(String key, Integer defaultValue) {
        String value = getValue(key);
        return StringUtil.convertInt(value, defaultValue);
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        String value = getValue(key);
        return StringUtil.convertLong(value, defaultValue);
    }

    @Override
    public Float getFloat(String key, Float defaultValue) {
        String value = getValue(key);
        return StringUtil.convertFloat(value, defaultValue);
    }

    @Override
    public Double getDouble(String key, Double defaultValue) {
        String value = getValue(key);
        return StringUtil.convertDouble(value, defaultValue);
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        String value = getValue(key);
        return StringUtil.convertBoolean(value, defaultValue);
    }

}
