package io.github.jihongkim98.redisextension;

import io.github.jihongkim98.redisextension.annotation.RedisBroadcastListener;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodIntrospector.MetadataLookup;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.redis.listener.Topic;

public class RedisBroadcastAnnotationBeanPostProcessor implements
        BeanPostProcessor, SmartInitializingSingleton, ApplicationContextAware {

    private final Log logger = LogFactory.getLog(RedisBroadcastAnnotationBeanPostProcessor.class);

    private final Set<Class<?>> nonAnnotatedClasses = Collections.synchronizedSet(new HashSet<>());

    private final ChannelRegistrar channelRegistrar = new ChannelRegistrar();

    private ApplicationContext applicationContext;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        if (nonAnnotatedClasses.contains(targetClass)) {
            return bean;
        }
        detectAnnotations(bean, targetClass);
        return bean;
    }

    private void detectAnnotations(Object bean, Class<?> targetClass) {
        Map<Method, RedisBroadcastListener> annotationMethods = MethodIntrospector.selectMethods(targetClass,
                (MetadataLookup<RedisBroadcastListener>) method ->
                        AnnotatedElementUtils.findMergedAnnotation(method, RedisBroadcastListener.class)
        );
        if (annotationMethods.isEmpty()) {
            nonAnnotatedClasses.add(targetClass);
            return;
        }
        annotationMethods.values().stream()
                .map(this::createTopic)
                .forEach(topic -> channelRegistrar.registerChannel(bean, topic));
    }

    private Topic createTopic(RedisBroadcastListener annotation) {
        // TODO

        return null;
    }

    @Override
    public void afterSingletonsInstantiated() {
        // TODO

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
