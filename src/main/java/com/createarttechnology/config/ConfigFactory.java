package com.createarttechnology.config;

import com.createarttechnology.logger.Logger;
import com.google.common.base.Preconditions;
import com.createarttechnology.jutil.StringUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.Watcher;

import java.io.IOException;

/**
 * Created by lixuhui on 2018/11/13.
 */
public abstract class ConfigFactory {

    private static final Logger logger = Logger.getLogger(ConfigFactory.class);

    private static CuratorFramework client;

    public static Config load(String configName, ConfigWatcher configWatcher) {
        Preconditions.checkArgument(StringUtil.isNotEmpty(configName));
        Preconditions.checkNotNull(configWatcher);

        BaseConfig config = new BaseConfig();
        try {
            if (client == null) {
                System.getProperties().load(ConfigFactory.class.getResourceAsStream("/process.properties"));
                String zookeeperConnectionString = System.getProperty("process.properties.zookeeperConnectionString");
                logger.info("process.properties.zookeeperConnectionString:{}", zookeeperConnectionString);
                RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
                client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
                client.start();
            }

            loadConfig(config, configName, configWatcher);
            client.getData().usingWatcher((Watcher) event -> {
                switch (event.getType()) {
                    case NodeDataChanged:
                    case NodeDeleted:
                        loadConfig(config, configName, configWatcher);
                        configWatcher.changed(config);
                        return;
                    default:
                }
            });
        } catch (IOException e) {
            logger.error("load config error, e:", e);
        }
        return config;
    }

    static void loadConfig(BaseConfig config, String configName, ConfigWatcher configWatcher) {
        try {
            client.blockUntilConnected();
            byte[] data = client.getData().forPath(getConfigPath(configName));
            if (data != null) {
                config.load(new String(data));
                configWatcher.changed(config);
            }
        } catch (Exception e) {
            logger.error("error, e:", e);
        }
    }

    private static String getConfigPath(String configName) {
        String profile = System.getProperty("process.properties.profile");
        logger.info("process.properties.profile:{}", profile);
        if (profile == null) {
            profile = "dev";
        }
        return "/config/" + profile + "/" + configName + "/" + configName;
    }

}
