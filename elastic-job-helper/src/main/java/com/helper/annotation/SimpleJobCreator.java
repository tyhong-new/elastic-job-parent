package com.helper.annotation;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.helper.bean.ProxySimpleJob;
import com.helper.config.ZookeeperEasyConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ApplicationListenerMethodAdapter;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.EventListenerFactory;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SimpleJobCreator implements ApplicationContextAware, InitializingBean {
    private final Log logger = LogFactory.getLog(getClass());
    private ConfigurableApplicationContext applicationContext;
    private final Set<Class<?>> nonAnnotatedClasses =
            Collections.newSetFromMap(new ConcurrentHashMap<Class<?>, Boolean>(64));

    private final Map<String, ProxySimpleJob> proxySimpleJobMap = new ConcurrentHashMap<>(32);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        getSimpleJobs();
    }

    public void getSimpleJobs() {//模仿EventListenerMethodProcessor做的
        String[] beanNames = this.applicationContext.getBeanNamesForType(Object.class);
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
                        processBean(beanName, type);
                    } catch (Throwable ex) {
                        throw new BeanInitializationException("Failed to process @EventListener " +
                                "annotation on bean with name '" + beanName + "'", ex);
                    }
                }
            }
        }
    }

    private void processBean(String beanName, Class<?> targetType) {
        if (!this.nonAnnotatedClasses.contains(targetType)) {
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
                this.nonAnnotatedClasses.add(targetType);
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
                String name = getName(beanName, methodToUse);
                proxySimpleJobMap.put(name, new ProxySimpleJob(targetInstance, method));
                CoordinatorRegistryCenter coordinatorRegistryCenter = this.applicationContext.getBean(CoordinatorRegistryCenter.class);
                new SpringJobScheduler(proxySimpleJobMap.get(name), coordinatorRegistryCenter, getJobConfig(name, methodToUse.getAnnotation(EasySimpleJob.class))).init();
            }
            if (logger.isDebugEnabled()) {
                logger.debug(annotatedMethods.size() + " @EventListener methods processed on bean '" +
                        beanName + "': " + annotatedMethods);
            }
        }
    }

    private LiteJobConfiguration getJobConfig(String name, EasySimpleJob easySimpleJob) {
        // 定义作业核心配置
        JobCoreConfiguration simpleCoreConfig = JobCoreConfiguration.newBuilder(name, easySimpleJob.cron(), easySimpleJob.shardingTotalCount())
                .description(easySimpleJob.description()).failover(easySimpleJob.failover()).jobParameter(easySimpleJob.jobParameter())
                .misfire(easySimpleJob.misfire()).shardingItemParameters(easySimpleJob.shardingItemParameters()).build();
        applicationContext.getBeanFactory().registerSingleton(name, proxySimpleJobMap.get(name));
        // 定义SIMPLE类型配置
        SimpleJobConfiguration simpleJobConfig = new SimpleJobConfiguration(simpleCoreConfig, name);
        // 定义Lite作业根配置
        LiteJobConfiguration simpleJobRootConfig = LiteJobConfiguration.newBuilder(simpleJobConfig).jobShardingStrategyClass(easySimpleJob.jobShardingStrategyClass())
                .maxTimeDiffSeconds(easySimpleJob.maxTimeDiffSeconds()).jobShardingStrategyClass(easySimpleJob.jobShardingStrategyClass())
                .monitorExecution(easySimpleJob.monitorExecution()).monitorPort(easySimpleJob.monitorPort()).reconcileIntervalMinutes(easySimpleJob.reconcileIntervalMinutes()).build();
        return simpleJobRootConfig;
    }

    private String getName(String beanName, Method method) {
        if (method.getParameterCount() == 0) {
            return String.format("SJ_%s_%s", beanName, method.getName());
        } else {
            return String.format("PSJ_%s_%s", beanName, method.getName());
        }
    }
}
