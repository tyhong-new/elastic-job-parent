package com.helper.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class EasyJobEventConfiguration implements EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public JobEventRdbConfiguration getJobEventConfiguration() {
        JobEventRdbConfiguration jobEventRdbConfig = new JobEventRdbConfiguration(getDruidDataSource());
        return jobEventRdbConfig;
    }

    private DataSource getDruidDataSource() {
        String url = getString("easy.datasource.url", getString("spring.datasource.url", ""));
        if (StringUtils.isBlank(url)) {
            return null;
        }
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        //dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        String username = getString("easy.datasource.username", getString("spring.datasource.username", ""));
        String password = getString("easy.datasource.password", getString("spring.datasource.password", ""));
        if (StringUtils.isNoneBlank(username)) {
            dataSource.setUsername(username);
        }
        if (StringUtils.isNoneBlank(password)) {
            dataSource.setPassword(password);
        }
        return dataSource;
    }

    private String getString(String name, String defaultValue) {
        return environment.getProperty(name, String.class, defaultValue);
    }
}
