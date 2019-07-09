package com.createarttechnology.config;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/**
 * 基于Properties的Config
 * Created by lixuhui on 2019/7/9.
 */
class PropertiesConfig extends AbstractConfig {

    private Properties properties;

    PropertiesConfig(String content) throws IOException {
        properties = new Properties();
        if (content != null) {
            properties.load(new StringReader(content));
        }
    }

    @Override
    String getValue(String key) {
        return properties.getProperty(key);
    }

}
