package com.helper.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EasySimpleJob {

    String jobName() default "";

    String cron();

    /**
     * 本地配置是否可覆盖注册中心配置
     * 如果可覆盖，每次启动作业都以本地配置为准
     * @return
     */
    boolean overwrite() default true;
    /**
     * 作业分片总数
     * @return
     */
    int shardingTotalCount() default 1;

    /**
     * 分片序列号和参数用等号分隔，多个键值对用逗号分隔分片序列号从0开始，
     * 不可大于或等于作业分片总数
     * 如：0=a,1=b,2=c
     *
     * @return
     */
    String shardingItemParameters() default "";

    /**
     * 作业自定义参数作业自定义参数，可通过传递该参数为作业调度的业务方法传参，
     * 用于实现带参数的作业
     * 例：每次获取的数据量、作业实例从数据库读取的主键等
     * @return
     */
    String jobParameter() default "";

    /**
     * 是否开启任务执行失效转移，开启表示如果作业在一次任务执行中途宕机，
     * 允许将该次未完成的任务在另一作业节点上补偿执行
     * @return
     */
    boolean failover() default false;

    /**
     * 是否开启错过任务重新执行
     * @return
     */
    boolean misfire() default false;

    /**
     * 作业描述信息
     * @return
     */
    String description() default "";

    /**
     * 配置jobProperties定义的枚举控制
     * Elastic-Job的实现细节JOB_EXCEPTION_HANDLER用于扩展异常处理类
     * EXECUTOR_SERVICE_HANDLER用于扩展作业处理线程池类
     * @return
     */
    String jobProperties() default "";


    boolean monitorExecution() default true;

    /**
     * 作业监控端口
     * 建议配置作业监控端口, 方便开发者dump作业信息。
     * 使用方法: echo “dump” | nc 127.0.0.1 9888
     * @return
     */
    int monitorPort() default -1;

    /**
     * 最大允许的本机与注册中心的时间误差秒数
     * 如果时间误差超过配置秒数则作业启动时将抛异常
     * 配置为-1表示不校验时间误差
     * @return
     */
    int maxTimeDiffSeconds() default -1;

    /**
     * 作业分片策略实现类全路径
     * 默认使用平均分配策略
     * @return
     */
    String jobShardingStrategyClass() default "";

    /**
     * 修复作业服务器不一致状态服务调度间隔时间，配置为小于1的任意值表示不执行修复
     * 单位：分钟
     * @return
     */
    int reconcileIntervalMinutes() default 10;

}

