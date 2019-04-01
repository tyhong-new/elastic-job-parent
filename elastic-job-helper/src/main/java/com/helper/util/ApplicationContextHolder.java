package com.helper.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextHolder implements ApplicationContextAware {

    private static ConfigurableApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.applicationContext = (ConfigurableApplicationContext) applicationContext;
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
