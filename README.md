## 简介
config包是一个用于监听并获取配置的工具包，目前的实现是基于ZooKeeper的。

## 项目结构
非原创。

配置文件通过ZooKeeper保存，数据修改需要通过ConfigServer（非本项目）或遵从当前的配置文件目录结构手动修改。

通过注册ZooKeeper节点CuratorWatcher的方式实现监听配置变化，并提供ConfigWatcher接口用于在业务中实现配置变化时的逻辑。

可以在不同的运行环境（namespace）下使用相同的配置文件名。

## 使用方法

### 1. 配置文件目录结构
在ZooKeeper上保存的配置文件为如下结构：
```text
/config/{profile}/{configName}/{configName}[{version}]
```
其中：
1. 一级目录固定为`/config`
2. 二级目录为运行环境，如dev、online等
3. 三级目录为配置文件名，如`mysql_config`
4. 四级（节点）为配置文件名+版本号，如`mysql_config000000`，`mysql_config000001`等，这些节点是该配置的历史版本
5. 每个配置文件目录下都有一个不带版本号的节点，全路径为`/config/dev/mysql_config/mysql_config`，这个节点是该配置的当前版本
6. 通过ConfigServer每次创建新配置文件时，会做如下操作：
    - 将配置的当前版本（不带版本号的节点）复制为当前目录下一个新的节点，版本号由ZooKeeper保证自增
    - 修改配置的当前版本

因此，在修改时记录了历史版本，便于回滚配置；另外，在读取配置时仅需要读取无版本号的节点即可。

### 2. 配置文件格式
配置文件的格式目前使用java的properties格式，即如下的kv形式：
```properties
#管理员登录用的配置
username=admin
password=password
```

### 2. 添加本地配置文件

需要在本地resources目录下添加process.properties文件，文件中应当包含如下内容：
1. ZooKeeper链接url和端口，如 `process.properties.zookeeperConnectionString=www.createarttechnology.com:2181`
2. 运行环境，如 `process.properties.profile=online`，需要与ZooKeeper上的二级目录对应

另外，需要在操作系统的环境变量中添加 `ZK_AUTH` 变量，用于ZooKeeper鉴权，这个需要与使用的ZooKeeper权限定义的一致，否则无法连接ZooKeeper

### 3. 添加maven依赖
```text
        <dependency>
            <groupId>com.createarttechnology</groupId>
            <artifactId>config</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
```

### 4. 在代码中注册ConfigWatcher

对外提供了ConfigFactory类、Config接口和ConfigWatcher接口，其中：
- Config：仅提供在ConfigWatcher的实现类中调用，获取配置数据
- ConfigWatcher：业务端实现，配置发生变化（修改或删除）时会回调
- ConfigFactory：仅提供load方法，ConfigWatcher的注册

典型的应用方式是将类属性与配置关联，并提供getter方法
```java
@Service
public class ConfigService {

    private static final Logger logger = Logger.getLogger(ConfigService.class);

    private static final String CONFIG_FILE = "admin.properties";

    private String username;
    private String password;

    @PostConstruct
    public void init() {
        // 注册ConfigWatcher
        ConfigFactory.load(CONFIG_FILE, new ConfigWatcher() {
            @Override
            public void changed(Config config) {
                // 实现配置变化时的逻辑
                username = config.getString("username", "admin");
                password = config.getString("password", "password");
                // 打印一下结果
                logger.info("username={}, password={}", username, password);
            }
        });
    }

    // 提供getter方法

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
```
