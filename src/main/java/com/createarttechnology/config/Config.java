package com.createarttechnology.config;

/**
 * 对外展示，不允许实例化
 * Created by lixuhui on 2018/11/13.
 */
public interface Config {

    String getString(String key, String defaultValue);

    Integer getInt(String key, Integer defaultValue);

    Long getLong(String key, Long defaultValue);

    Float getFloat(String key, Float defaultValue);

    Double getDouble(String key, Double defaultValue);

    Boolean getBoolean(String key, Boolean defaultValue);

}
