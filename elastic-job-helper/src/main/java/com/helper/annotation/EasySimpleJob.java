package com.helper.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EasySimpleJob {

    String jobName() default "";

    String cron();

    /**
     * 作业分片总数
     * @return
     */
    int shardingTotalCount() default 1;

    String shardingItemParameters() default "";

    String jobParameter() default "";

    boolean failover() default false;

    boolean misfire() default false;

    String description() default "";

    String jobProperties() default "";


    boolean streamingProcess() default false;


    boolean monitorExecution() default true;

    int monitorPort() default -1;

    int maxTimeDiffSeconds() default -1;

    String jobShardingStrategyClass() default "";

    int reconcileIntervalMinutes() default 10;

    String eventTraceRdbDataSource() default "";

    boolean local() default false;
}

