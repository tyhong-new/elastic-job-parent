package com.helper.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.helper.util.ApplicationContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

/**
 * 事件追踪配置，非必须配置项
 */
@Configuration
public class EasyJobEventConfiguration {

    @Bean
    @DependsOn("applicationContextHolder")
    public JobEventRdbConfiguration getJobEventConfiguration() {
        JobEventRdbConfiguration jobEventRdbConfig = new JobEventRdbConfiguration(getDruidDataSource());
        return jobEventRdbConfig;
    }

    private DataSource getDruidDataSource() {
        String url = ApplicationContextHolder.getStringProperty("easy.datasource.url", ApplicationContextHolder.getStringProperty("spring.datasource.url", ""));
        if (StringUtils.isBlank(url)) {
            return null;
        }
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        //dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        String driverClass = ApplicationContextHolder.getStringProperty("easy.datasource.driver-class-name", ApplicationContextHolder.getStringProperty("spring.datasource.driver-class-name", EasyDefaultConfigConstants.DRIVER_CLASS_NAME));
        dataSource.setDriverClassName(driverClass);
        String username = ApplicationContextHolder.getStringProperty("easy.datasource.username", ApplicationContextHolder.getStringProperty("spring.datasource.username", ""));
        String password = ApplicationContextHolder.getStringProperty("easy.datasource.password", ApplicationContextHolder.getStringProperty("spring.datasource.password", ""));
        if (StringUtils.isNoneBlank(username)) {
            dataSource.setUsername(username);
        }
        if (StringUtils.isNoneBlank(password)) {
            dataSource.setPassword(password);
        }
        return dataSource;
    }

}
