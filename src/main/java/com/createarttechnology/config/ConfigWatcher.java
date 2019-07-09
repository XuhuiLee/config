package com.createarttechnology.config;

/**
 * Config监听接口，用于实现初次读取配置或配置发生变化时的逻辑
 * Created by lixuhui on 2018/11/13.
 */
public interface ConfigWatcher {

    void changed(Config config);

}
