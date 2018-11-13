package com.createarttechnology.config;

import com.createarttechnology.jutil.StringUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

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
                    ignored.printStackTrace();
                }
            }
        }
    }

    private void loadConfig(String configName) throws Exception {
        Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream("/" + configName));
        System.setProperties(properties);
    }

}
