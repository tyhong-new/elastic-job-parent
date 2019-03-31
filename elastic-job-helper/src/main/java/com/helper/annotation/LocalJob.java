package com.helper.annotation;

import java.lang.annotation.*;

/**
 * Created by tyhong on 2019/3/31.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LocalJob {
}
