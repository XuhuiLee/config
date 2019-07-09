package com.createarttechnology.config;

/**
 * 用于兜底的空白Config
 * Created by lixuhui on 2019/7/9.
 */
class EmptyConfig extends AbstractConfig {

    @Override
    String getValue(String key) {
        return null;
    }

}
