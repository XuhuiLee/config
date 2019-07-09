package com.createarttechnology.config;

import com.createarttechnology.jutil.StringUtil;
import com.createarttechnology.logger.Logger;
import com.google.common.base.Preconditions;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.IOException;

/**
 * Config工厂，用于建立ZooKeeper连接、添加Watcher及监听Config变化
 * Created by lixuhui on 2018/11/13.
 */
public abstract class ConfigFactory {

    private static final Logger logger = Logger.getLogger(ConfigFactory.class);

    // zookeeper客户端单例，后续可改用资源池
    private static volatile CuratorFramework client;
    // 运行环境，如dev、online
    private static String profile;

    /**
     * Double Check Lock 初始化单例
     */
    private static void initClient() {
        synchronized (ConfigFactory.class) {
            if (client == null) {
                // 从process.properties读取zookeeper连接配置
                try {
                    System.getProperties().load(ConfigFactory.class.getResourceAsStream("/process.properties"));
                } catch (IOException e) {
                    logger.error("get client error, e:", e);
                }
                String zookeeperConnectionString = System.getProperty("process.properties.zookeeperConnectionString", "www.createarttechnology.com:2181");
                // 根据本地配置文件获取运行环境
                profile = System.getProperty("process.properties.profile", "dev");
                // 避免鉴权信息泄露，直接使用系统变量
                String zkAuth = System.getenv("ZK_AUTH");

                // 建立连接
                client = CuratorFrameworkFactory
                        .builder()
                        .authorization("digest", zkAuth.getBytes())
                        .connectString(zookeeperConnectionString)
                        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                        .build();
                client.start();

                logger.info("[zookeeperConnectionString:{}, profile:{}]", zookeeperConnectionString, profile);
            }
        }
    }

    /**
     * 读取配置并注册Watcher
     */
    public static void load(String configName, ConfigWatcher configWatcher) {
        Preconditions.checkArgument(StringUtil.isNotEmpty(configName));
        Preconditions.checkNotNull(configWatcher);

        // Double Check Lock 初始化单例
        if (client == null) {
            initClient();
        }

        // 读配置
        Config config = loadConfig(configName);

        // 回调watcher，并重复添加watcher，因为每个CuratorWatcher只生效一次
        configWatcher.changed(config);
        try {
            client.getData().usingWatcher((CuratorWatcher) event -> {
                switch (event.getType()) {
                    case NodeDataChanged:
                    case NodeDeleted:
                        load(configName, configWatcher);
                        return;
                    default:
                }
            }).forPath(getConfigPath(configName));
        } catch (Exception e) {
            logger.error("use watcher error, e:", e);
        }
    }

    /**
     * 实际读取配置
     */
    private static Config loadConfig(String configName) {
        try {
            client.blockUntilConnected();
            byte[] data = client.getData().forPath(getConfigPath(configName));
            if (data != null) {
                return new PropertiesConfig(new String(data));
            }
        } catch (Exception e) {
            logger.error("loadConfig error, e:", e);
        }
        return new EmptyConfig();
    }

    /**
     * 基于配置的保存结构生成读取结构
     */
    private static String getConfigPath(String configName) {
        return "/config/" + profile + "/" + configName + "/" + configName;
    }

}
