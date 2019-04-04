package com.helper.starter.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.helper.starter.util.ApplicationContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * 事件追踪配置，非必须配置项
 */
@ConfigurationProperties
//@EnableConfigurationProperties(EasyConfig.class)//这个如果其他类没有激活，则需要
@AutoConfigureAfter(EasyConfig.class)//这个必须要
public class EasyJobEventConfiguration {

    @Autowired
    private EasyConfig easyConfig;

    public EasyJobEventConfiguration() {
        System.out.println("EasyJobEventConfiguration");
    }

    @Bean
    public JobEventRdbConfiguration getJobEventConfiguration() {
        JobEventRdbConfiguration jobEventRdbConfig = new JobEventRdbConfiguration(getDruidDataSource());
        return jobEventRdbConfig;
    }

    private DataSource getDruidDataSource() {
        String url = getStringProperty(easyConfig.getDatasource().getUrl(), "spring.datasource.url", "");
        if (StringUtils.isBlank(url)) {
            return null;
        }
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        //dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        String driverClass = getStringProperty(easyConfig.getDatasource().getDriverClassName(), "spring.datasource.driver-class-name", EasyDefaultConfigConstants.DRIVER_CLASS_NAME);
        dataSource.setDriverClassName(driverClass);
        String username = getStringProperty(easyConfig.getDatasource().getUsername(), "spring.datasource.username", "");
        String password = getStringProperty(easyConfig.getDatasource().getPassword(), "spring.datasource.password", "");
        if (StringUtils.isNoneBlank(username)) {
            dataSource.setUsername(username);
        }
        if (StringUtils.isNoneBlank(password)) {
            dataSource.setPassword(password);
        }
        return dataSource;
    }

    private String getStringProperty(String value, String name, String defaultValue) {
        if (StringUtils.isNoneBlank(value)) {
            return value;
        }
        return ApplicationContextHolder.getStringProperty(name, defaultValue);
    }
}
