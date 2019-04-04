package com.helper.starter.config;

import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * zookeeper注册中心
 */
@ConfigurationProperties
//@EnableConfigurationProperties(EasyConfig.class)//这个如果其他类没有激活，则需要
@AutoConfigureAfter(EasyConfig.class)//这个必须要
public class ZookeeperEasyConfig {

    public ZookeeperEasyConfig(){
        System.out.println("ZookeeperEasyConfig");
    }
    @Autowired
    private EasyConfig easyConfig;

    @Bean
    public CoordinatorRegistryCenter getCoordinatorRegistryCenter() {
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(easyConfig.getZoo().getServerLists(), easyConfig.getZoo().getNamespace());
        zookeeperConfiguration.setDigest(easyConfig.getZoo().getDigest());
        zookeeperConfiguration.setBaseSleepTimeMilliseconds(easyConfig.getZoo().getBaseSleepTimeMilliseconds());
        zookeeperConfiguration.setConnectionTimeoutMilliseconds(easyConfig.getZoo().getConnectionTimeoutMilliseconds());
        zookeeperConfiguration.setMaxRetries(easyConfig.getZoo().getMaxRetries());
        zookeeperConfiguration.setMaxSleepTimeMilliseconds(easyConfig.getZoo().getMaxSleepTimeMilliseconds());
        zookeeperConfiguration.setSessionTimeoutMilliseconds(easyConfig.getZoo().getSessionTimeoutMilliseconds());
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
        regCenter.init();
        return regCenter;
    }
}
