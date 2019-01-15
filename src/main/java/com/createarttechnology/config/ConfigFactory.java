package com.createarttechnology.config;

import com.createarttechnology.jutil.StringUtil;
import com.createarttechnology.logger.Logger;
import com.google.common.base.Preconditions;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by lixuhui on 2018/11/13.
 */
public abstract class ConfigFactory {

    private static final Logger logger = Logger.getLogger(ConfigFactory.class);

    private static CuratorFramework client;

    private static String profile = System.getProperty("process.properties.profile");

    public static Config load(String configName, ConfigWatcher configWatcher) {
        Preconditions.checkArgument(StringUtil.isNotEmpty(configName));
        Preconditions.checkNotNull(configWatcher);

        BaseConfig config = new BaseConfig();
        try {
            // 初始化
            if (client == null) {
                System.getProperties().load(ConfigFactory.class.getResourceAsStream("/process.properties"));
                String zookeeperConnectionString = System.getProperty("process.properties.zookeeperConnectionString");
                logger.info("process.properties.zookeeperConnectionString:{}", zookeeperConnectionString);
                RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
                String zkAuth = System.getenv("ZK_AUTH");
                client = CuratorFrameworkFactory
                        .builder()
                        .authorization("digest", zkAuth.getBytes())
                        .connectString(zookeeperConnectionString)
                        .retryPolicy(retryPolicy)
                        .build();
                client.start();
                if (profile == null) {
                    profile = "dev";
                }
                logger.info("process.properties.profile:{}", profile);
            }
            // 先读一次
            loadConfig(config, configName, configWatcher);
        } catch (Exception e) {
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
            } else {
                config.load("");
            }
            // 客户端watcher回调
            configWatcher.changed(config);
        } catch (Exception e) {
            logger.error("loadConfig error, e:", e);
        }
        // 每次都注册watcher
        try {
            client.getData().usingWatcher((CuratorWatcher) event -> {
                switch (event.getType()) {
                    case NodeDataChanged:
                    case NodeDeleted:
                        loadConfig(config, configName, configWatcher);
                        return;
                    default:
                }
            }).forPath(getConfigPath(configName));
        } catch (Exception e) {
            logger.error("getData error, e:", e);
        }
    }

    private static String getConfigPath(String configName) {
        return "/config/" + profile + "/" + configName + "/" + configName;
    }

}
