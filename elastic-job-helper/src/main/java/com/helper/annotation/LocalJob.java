package com.helper.annotation;

import java.lang.annotation.*;

/**
 * 本地job，每个实例都会运行；可以使用在清理本地缓存等地方
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LocalJob {
}
