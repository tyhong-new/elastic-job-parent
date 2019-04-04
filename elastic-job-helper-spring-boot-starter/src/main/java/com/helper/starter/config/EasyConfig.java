package com.helper.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;


import static com.helper.starter.config.EasyDefaultConfigConstants.*;

/**
 * 所有的参数
 */
@ConfigurationProperties(prefix = "easy")
public class EasyConfig {
    /**
     * 这个注解的作用是
     * 在生成spring-configuration-metadata.json（一个让idea知道点击配置文件参数跳转位置的文件）时
     * 能解析这个实体属性的内置属性。如zoo.namespace；如果没加只会有个zoo
     */
    @NestedConfigurationProperty
    private ZooConfig zoo;
    @NestedConfigurationProperty
    private EasyDataSourceConfig datasource;

    public EasyConfig(){
        System.out.println("EasyConfig");
    }
    public ZooConfig getZoo() {
        return zoo;
    }

    public void setZoo(ZooConfig zoo) {
        this.zoo = zoo;
    }

    public EasyDataSourceConfig getDatasource() {
        return datasource;
    }

    public void setDatasource(EasyDataSourceConfig datasource) {
        this.datasource = datasource;
    }

    public static class EasyDataSourceConfig{
        private String url;
        private String driverClassName;
        private String username;
        private String password;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class ZooConfig {
        private String serverLists = ZOO_SERVER_LISTS;
        private String namespace = ZOO_NAMESPACE;
        private String digest = ZOO_DIGEST;
        private int baseSleepTimeMilliseconds = ZOO_BASE_SLEEP_TIME_MILLISECONDS;
        private int connectionTimeoutMilliseconds = ZOO_CONNECTION_TIMEOUT_MILLISECONDS;
        private int maxRetries = ZOO_MAX_RETRIES;
        private int maxSleepTimeMilliseconds = ZOO_MAX_SLEEP_TIME_MILLISECONDS;
        private int sessionTimeoutMilliseconds = ZOO_SESSION_TIMEOUT_MILLISECONDS;

        public String getServerLists() {
            return serverLists;
        }

        public void setServerLists(String serverLists) {
            this.serverLists = serverLists;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getDigest() {
            return digest;
        }

        public void setDigest(String digest) {
            this.digest = digest;
        }

        public int getBaseSleepTimeMilliseconds() {
            return baseSleepTimeMilliseconds;
        }

        public void setBaseSleepTimeMilliseconds(int baseSleepTimeMilliseconds) {
            this.baseSleepTimeMilliseconds = baseSleepTimeMilliseconds;
        }

        public int getConnectionTimeoutMilliseconds() {
            return connectionTimeoutMilliseconds;
        }

        public void setConnectionTimeoutMilliseconds(int connectionTimeoutMilliseconds) {
            this.connectionTimeoutMilliseconds = connectionTimeoutMilliseconds;
        }

        public int getMaxRetries() {
            return maxRetries;
        }

        public void setMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
        }

        public int getMaxSleepTimeMilliseconds() {
            return maxSleepTimeMilliseconds;
        }

        public void setMaxSleepTimeMilliseconds(int maxSleepTimeMilliseconds) {
            this.maxSleepTimeMilliseconds = maxSleepTimeMilliseconds;
        }

        public int getSessionTimeoutMilliseconds() {
            return sessionTimeoutMilliseconds;
        }

        public void setSessionTimeoutMilliseconds(int sessionTimeoutMilliseconds) {
            this.sessionTimeoutMilliseconds = sessionTimeoutMilliseconds;
        }
    }
}
