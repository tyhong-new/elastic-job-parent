package com.helper.config;

import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.helper.util.ApplicationContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import static com.helper.config.EasyDefaultConfigConstants.*;

/**
 * zookeeper注册中心
 */
@Configuration
public class ZookeeperEasyConfig {


    @Bean
    @DependsOn("applicationContextHolder")
    public CoordinatorRegistryCenter getCoordinatorRegistryCenter() {
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(ApplicationContextHolder.getStringProperty("easy.zoo.serverLists", ZOO_SERVER_LISTS), ApplicationContextHolder.getStringProperty("easy.zoo.namespace", ZOO_NAMESPACE));
        zookeeperConfiguration.setDigest(ApplicationContextHolder.getStringProperty("easy.zoo.digest", ZOO_DIGEST));
        zookeeperConfiguration.setBaseSleepTimeMilliseconds(ApplicationContextHolder.getIntProperty("easy.zoo.baseSleepTimeMilliseconds", ZOO_BASE_SLEEP_TIME_MILLISECONDS));
        zookeeperConfiguration.setConnectionTimeoutMilliseconds(ApplicationContextHolder.getIntProperty("easy.zoo.connectionTimeoutMilliseconds", ZOO_CONNECTION_TIMEOUT_MILLISECONDS));
        zookeeperConfiguration.setMaxRetries(ApplicationContextHolder.getIntProperty("easy.zoo.maxRetries", ZOO_MAX_RETRIES));
        zookeeperConfiguration.setMaxSleepTimeMilliseconds(ApplicationContextHolder.getIntProperty("easy.zoo.maxSleepTimeMilliseconds", ZOO_MAX_SLEEP_TIME_MILLISECONDS));
        zookeeperConfiguration.setSessionTimeoutMilliseconds(ApplicationContextHolder.getIntProperty("easy.zoo.sessionTimeoutMilliseconds", ZOO_SESSION_TIMEOUT_MILLISECONDS));
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
        regCenter.init();
        return regCenter;
    }
}
