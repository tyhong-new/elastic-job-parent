package com.helper.config;

public final class EasyDefaultConfigConstants {

    //ZookeeperConfiguration
    /**
     * 多个地址用逗号分隔,如: host1:2181,host2:2181
     */
    public static final String ZOO_SERVER_LISTS = "host1:2181";
    /**
     * Zookeeper的命名空间
     */
    public static final String ZOO_NAMESPACE = null;
    /**
     * 等待重试的间隔时间的初始值,单位：毫秒
     */
    public static final int ZOO_BASE_SLEEP_TIME_MILLISECONDS = 1000;
    /**
     * 等待重试的间隔时间的最大值,单位：毫秒
     */
    public static final int ZOO_MAX_SLEEP_TIME_MILLISECONDS = 3000;

    /**
     * 最大重试次数
     */
    public static final int ZOO_MAX_RETRIES = 3;

    /**
     * 会话超时时间,单位：毫秒
     */
    public static final int ZOO_SESSION_TIMEOUT_MILLISECONDS = 60000;

    /**
     * 连接超时时间,单位：毫秒
     */
    public static final int ZOO_CONNECTION_TIMEOUT_MILLISECONDS = 15000;


    /**
     * 连接Zookeeper的权限令牌,缺省为不需要权限验证
     */
    public static final String ZOO_DIGEST = null;




    private EasyDefaultConfigConstants() {
    }
}
