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
        if (method.getParameterCount() > 1) {
            throw new RuntimeException(method.getDeclaringClass() + "." + method.getName() + "参数最多只能一个");
        }
        if (method.getParameterCount() == 1 && !method.getParameterTypes()[0].isAssignableFrom(ShardingContext.class)) {
            throw new RuntimeException(method.getDeclaringClass() + "." + method.getName() + "参数最多只能一个,且只能是ShardingContext");
        }
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
