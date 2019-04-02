package com.helper.annotation;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.helper.bean.ProxySimpleJob;
import com.helper.strategy.LocalJobStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 创建SimpleJobScheduler，核心类
 */
@Component
public class SimpleJobCreator implements ApplicationContextAware, InitializingBean {

    private final Log logger = LogFactory.getLog(getClass());
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        createSimpleJobs();
    }

    private void createSimpleJobs() {//模仿EventListenerMethodProcessor做的
        String[] beanNames = this.applicationContext.getBeanNamesForType(Object.class);
        //获取事件配置
        JobEventRdbConfiguration jobEventRdbConfiguration = this.applicationContext.getBean(JobEventRdbConfiguration.class);
        //缓存没命中的
        Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<Class<?>, Boolean>(64));
        //缓存jobName，避免出现名字一样的情况
        Set<String> jobNameSet = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(64));
        //先存着scheduler，避免创建到一半，报错后，一些任务还在跑
        List<SpringJobScheduler> schedulers = new LinkedList<>();
        for (String beanName : beanNames) {
            if (!ScopedProxyUtils.isScopedTarget(beanName)) {
                Class<?> type = null;
                try {
                    type = AutoProxyUtils.determineTargetClass(this.applicationContext.getBeanFactory(), beanName);
                } catch (Throwable ex) {
                    // An unresolvable bean type, probably from a lazy bean - let's ignore it.
                    if (logger.isDebugEnabled()) {
                        logger.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
                    }
                }
                if (type != null) {
                    if (ScopedObject.class.isAssignableFrom(type)) {
                        try {
                            type = AutoProxyUtils.determineTargetClass(this.applicationContext.getBeanFactory(),
                                    ScopedProxyUtils.getTargetBeanName(beanName));
                        } catch (Throwable ex) {
                            // An invalid scoped proxy arrangement - let's ignore it.
                            if (logger.isDebugEnabled()) {
                                logger.debug("Could not resolve target bean for scoped proxy '" + beanName + "'", ex);
                            }
                        }
                    }
                    try {
                        processBean(beanName, type, jobEventRdbConfiguration, nonAnnotatedClasses, schedulers, jobNameSet);
                    } catch (Throwable ex) {
                        throw new BeanInitializationException("Failed to process @EventListener " +
                                "annotation on bean with name '" + beanName + "'", ex);
                    }
                }
            }
        }
        schedulers.forEach(SpringJobScheduler::init);
    }

    private void processBean(String beanName, Class<?> targetType, JobEventRdbConfiguration jobEventRdbConfiguration, Set<Class<?>> nonAnnotatedClasses,
                             List<SpringJobScheduler> schedulers, Set<String> jobNameSet) {
        if (nonAnnotatedClasses.contains(targetType)) {
            return;
        }
        Map<Method, EasySimpleJob> annotatedMethods = null;
        try {
            annotatedMethods = MethodIntrospector.selectMethods(targetType, (Method method) -> AnnotatedElementUtils.findMergedAnnotation(method, EasySimpleJob.class));
        } catch (Throwable ex) {
            // An unresolvable type in a method signature, probably from a lazy bean - let's ignore it.
            if (logger.isDebugEnabled()) {
                logger.debug("Could not resolve methods for bean with name '" + beanName + "'", ex);
            }
        }

        if (CollectionUtils.isEmpty(annotatedMethods)) {
            nonAnnotatedClasses.add(targetType);
            if (logger.isTraceEnabled()) {
                logger.trace("No @EventListener annotations found on bean class: " + targetType);
            }
            return;
        }
        // Non-empty set of methods
        for (Method method : annotatedMethods.keySet()) {
            Method methodToUse = AopUtils.selectInvocableMethod(
                    method, this.applicationContext.getType(beanName));

            Object targetInstance = this.applicationContext.getBean(beanName);
            EasySimpleJob easySimpleJob = methodToUse.getAnnotation(EasySimpleJob.class);
            //处理名字
            String name = getName(easySimpleJob.jobName(), beanName, methodToUse);
            if (jobNameSet.contains(name)) {
                throw new RuntimeException("job名字【" + name + "】重复");
            }
            jobNameSet.add(name);
            //创建代理类
            ProxySimpleJob proxySimpleJob = new ProxySimpleJob(targetInstance, method);
            applicationContext.getBeanFactory().registerSingleton(name, proxySimpleJob);

            //获取注册中心配置
            CoordinatorRegistryCenter coordinatorRegistryCenter = this.applicationContext.getBean(CoordinatorRegistryCenter.class);

            //获取job监听器
            Map<String, ElasticJobListener> listenerMap = this.applicationContext.getBeansOfType(ElasticJobListener.class);
            ElasticJobListener[] listeners = new ElasticJobListener[listenerMap.size()];
            listenerMap.values().toArray(listeners);

            //创建SpringJobScheduler
            LiteJobConfiguration jobConfiguration = getJobConfig(name, easySimpleJob, methodToUse.getAnnotation(LocalJob.class));
            if (jobEventRdbConfiguration != null && jobEventRdbConfiguration.getDataSource() != null) {
                schedulers.add(new SpringJobScheduler(proxySimpleJob, coordinatorRegistryCenter, jobConfiguration, jobEventRdbConfiguration, listeners));
            } else {
                schedulers.add(new SpringJobScheduler(proxySimpleJob, coordinatorRegistryCenter, jobConfiguration, listeners));
            }
        }
    }

    /**
     * 根据注解，创建job配置
     *
     * @param name
     * @param easySimpleJob
     * @param localJob
     * @return
     */
    private LiteJobConfiguration getJobConfig(String name, EasySimpleJob easySimpleJob, LocalJob localJob) {
        // 定义作业核心配置
        JobCoreConfiguration simpleCoreConfig = JobCoreConfiguration.newBuilder(name, easySimpleJob.cron(), easySimpleJob.shardingTotalCount())
                .description(easySimpleJob.description()).failover(easySimpleJob.failover()).jobParameter(easySimpleJob.jobParameter())
                .misfire(easySimpleJob.misfire()).shardingItemParameters(easySimpleJob.shardingItemParameters()).build();
        // 定义SIMPLE类型配置
        SimpleJobConfiguration simpleJobConfig = new SimpleJobConfiguration(simpleCoreConfig, name);
        //处理localJob
        String jobShardingStrategyClass = localJob == null ? StringUtils.isBlank(easySimpleJob.jobShardingStrategyClass()) ? null : easySimpleJob.jobShardingStrategyClass() : LocalJobStrategy.class.getCanonicalName();
        // 定义Lite作业根配置
        LiteJobConfiguration simpleJobRootConfig = LiteJobConfiguration.newBuilder(simpleJobConfig).jobShardingStrategyClass(easySimpleJob.jobShardingStrategyClass())
                .maxTimeDiffSeconds(easySimpleJob.maxTimeDiffSeconds()).jobShardingStrategyClass(jobShardingStrategyClass)
                .monitorExecution(easySimpleJob.monitorExecution()).monitorPort(easySimpleJob.monitorPort())
                .reconcileIntervalMinutes(easySimpleJob.reconcileIntervalMinutes()).overwrite(easySimpleJob.overwrite()).build();
        return simpleJobRootConfig;
    }

    /**
     * 定义job的名字
     *
     * @param jobName
     * @param beanName
     * @param method
     * @return
     */
    private String getName(String jobName, String beanName, Method method) {
        if (StringUtils.isBlank(jobName)) {
            if (method.getParameterCount() == 0) {
                return String.format("SJ_%s_%s", beanName, method.getName());
            } else {
                return String.format("PSJ_%s_%s", beanName, method.getName());
            }
        } else {
            if (method.getParameterCount() == 0) {
                return String.format("SJ_%s", jobName);
            } else {
                return String.format("PSJ_%s", jobName);
            }
        }
    }
}
