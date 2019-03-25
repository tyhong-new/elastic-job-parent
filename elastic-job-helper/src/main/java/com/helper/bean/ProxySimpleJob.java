package com.helper.bean;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;

public class ProxySimpleJob implements SimpleJob {

    private final Log logger = LogFactory.getLog(getClass());

    private Object targetInstance;
    private Method method;

    public ProxySimpleJob(Object targetInstance, Method method) {
        this.method = method;
        this.targetInstance = targetInstance;
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            if (method.getParameterCount() == 1) {
                method.invoke(targetInstance, shardingContext);
            } else {
                method.invoke(targetInstance);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
