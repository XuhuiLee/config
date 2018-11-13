package com.createarttechnology.config;

import com.google.common.base.Preconditions;
import jutil.StringUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.Watcher;

/**
 * Created by lixuhui on 2018/11/13.
 */
public abstract class ConfigFactory {

    private static CuratorFramework client;

    public static Config getConfig(String configName, ConfigWatcher configWatcher) {
        Preconditions.checkArgument(StringUtil.isNotEmpty(configName));
        Preconditions.checkNotNull(configWatcher);

        if (client == null) {
            String zookeeperConnectionString = "www.createarttechnology.com:2181";
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
            client.start();
        }

        BaseConfig config = new BaseConfig();
        loadConfig(config, configName);
        client.getData().usingWatcher((Watcher) event -> {
            switch (event.getType()) {
                case NodeDataChanged:
                case NodeDeleted:
                    loadConfig(config, configName);
                    configWatcher.changed(config);
            }
        });
        return config;
    }

    static void loadConfig(BaseConfig config, String configName) {
        try {
            client.blockUntilConnected();
            byte[] data = client.getData().forPath(getConfigPath(configName));
            if (data != null) {
                config.load(new String(data));
            }
        } catch (Exception ignored) {
        }
    }

    private static String getConfigPath(String configName) {
        String profile = System.getProperty("process.properties.profile");
        if (profile == null) {
            profile = "dev";
        }
        return "/root/" + profile + "/" + configName + "/" + configName;
    }

}
