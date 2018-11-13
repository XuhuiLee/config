package com.createarttechnology.config;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.createarttechnology.jutil.StringUtil;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Set;

/**
 * Created by lixuhui on 2018/11/13.
 */
public class StaticConfigManager {

    public void setConfigs(String configs) {
        if (StringUtil.isNotEmpty(configs)) {
            Set<String> configNameSet = Sets.newHashSet(Splitter.on(',').omitEmptyStrings().trimResults().split(configs));
            for (String configName : configNameSet) {
                try {
                    loadConfig(configName);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void loadConfig(String configName) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream(configName));
        System.setProperties(properties);
    }

}
