package com.helper.starter.annotation;

import java.lang.annotation.*;

/**
 * 标识，是否使用EasyJob;如果使用在启动类上，添加该注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableEasyJobConfiguration {
}
