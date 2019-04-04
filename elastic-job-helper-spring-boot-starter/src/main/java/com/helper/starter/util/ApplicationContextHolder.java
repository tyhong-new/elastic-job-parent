package com.helper.starter.util;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

/**
 * ApplicationContext工具类
 */
public class ApplicationContextHolder {

    private static ConfigurableApplicationContext applicationContext;

    private ApplicationContextHolder() {
    }

    public static void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        if (ApplicationContextHolder.applicationContext == null) {
            ApplicationContextHolder.applicationContext = applicationContext;
        }
    }

    public static ConfigurableApplicationContext getConfigurableApplicationContext() {
        return applicationContext;
    }

    public static Environment getEnvironment() {
        return applicationContext.getEnvironment();
    }

    public static int getIntProperty(String name, int defaultValue) {
        return applicationContext.getEnvironment().getProperty(name, Integer.class, defaultValue);
    }

    public static String getStringProperty(String name, String defaultValue) {
        return applicationContext.getEnvironment().getProperty(name, String.class, defaultValue);
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}
