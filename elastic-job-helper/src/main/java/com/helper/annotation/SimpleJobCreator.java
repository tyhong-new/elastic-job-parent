package com.helper.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
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
import java.util.Map;

@Component
public class SimpleJobCreator implements ApplicationContextAware {
    private final Log logger = LogFactory.getLog(getClass());
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    public void getSimpleJobs() {//模仿EventListenerMethodProcessor做的
        //List<EventListenerFactory> factories = getEventListenerFactories();
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
            Map<Method, EventListener> annotatedMethods = null;
            try {
                annotatedMethods = MethodIntrospector.selectMethods(targetType,(Method method)-> AnnotatedElementUtils.findMergedAnnotation(method, EventListener.class));

            }
            catch (Throwable ex) {
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
            }
            else {
                // Non-empty set of methods
                for (Method method : annotatedMethods.keySet()) {
                    for (EventListenerFactory factory : factories) {
                        if (factory.supportsMethod(method)) {
                            Method methodToUse = AopUtils.selectInvocableMethod(
                                    method, this.applicationContext.getType(beanName));
                            ApplicationListener<?> applicationListener =
                                    factory.createApplicationListener(beanName, targetType, methodToUse);
                            if (applicationListener instanceof ApplicationListenerMethodAdapter) {
                                ((ApplicationListenerMethodAdapter) applicationListener)
                                        .init(this.applicationContext, this.evaluator);
                            }
                            this.applicationContext.addApplicationListener(applicationListener);
                            break;
                        }
                    }
                }
                if (logger.isDebugEnabled()) {
                    logger.debug(annotatedMethods.size() + " @EventListener methods processed on bean '" +
                            beanName + "': " + annotatedMethods);
                }
            }
        }
    }

}
