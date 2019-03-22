package com.helper.config;

import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import static com.helper.config.EasyDefaultConfigConstants.*;


@Configuration
public class ZookeeperEasyConfig implements EnvironmentAware {

    private Environment environment;


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public CoordinatorRegistryCenter getCoordinatorRegistryCenter() {
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(getString("easy.zoo.serverLists", ZOO_SERVER_LISTS), getString("easy.zoo.namespace", ZOO_NAMESPACE));
        zookeeperConfiguration.setDigest(getString("easy.zoo.digest", ZOO_DIGEST));
        zookeeperConfiguration.setBaseSleepTimeMilliseconds(getInt("easy.zoo.baseSleepTimeMilliseconds", ZOO_BASE_SLEEP_TIME_MILLISECONDS));
        zookeeperConfiguration.setConnectionTimeoutMilliseconds(getInt("easy.zoo.connectionTimeoutMilliseconds", ZOO_CONNECTION_TIMEOUT_MILLISECONDS));
        zookeeperConfiguration.setMaxRetries(getInt("easy.zoo.connectionTimeoutMilliseconds", ZOO_MAX_RETRIES));
        zookeeperConfiguration.setMaxSleepTimeMilliseconds(getInt("easy.zoo.maxSleepTimeMilliseconds", ZOO_MAX_SLEEP_TIME_MILLISECONDS));
        zookeeperConfiguration.setSessionTimeoutMilliseconds(getInt("easy.zoo.sessionTimeoutMilliseconds", ZOO_SESSION_TIMEOUT_MILLISECONDS));
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
        regCenter.init();
        return regCenter;
    }

    private int getInt(String name, int defaultValue) {
        return environment.getProperty(name, Integer.class, defaultValue);
    }

    private String getString(String name, String defaultValue) {
        return environment.getProperty(name, String.class, defaultValue);
    }
}
