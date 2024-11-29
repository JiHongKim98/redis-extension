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
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.util.Assert;

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
        }

        annotationMethods.forEach((key, value) -> processAnnotation(bean, key, value));
    }

    private void processAnnotation(Object bean, Method method, RedisBroadcastListener annotation) {
        String[] channels = annotation.channels();
        String[] patterns = annotation.patterns();

        boolean hasChannels = hasValidValues(channels);
        boolean hasPatterns = hasValidValues(patterns);

        Assert.state(hasChannels || hasPatterns,
                "Either 'channels' or 'patterns' must be defined and must not contain empty values.");

        if (hasChannels) {
            for (String channel : channels) {
                ChannelTopic channelTopic = new ChannelTopic(channel);
                this.channelRegistrar.registerChannel(bean, method, channelTopic);
            }
        }

        if (hasPatterns) {
            for (String pattern : patterns) {
                PatternTopic patternTopic = new PatternTopic(pattern);
                this.channelRegistrar.registerChannel(bean, method, patternTopic);
            }
        }
    }

    private boolean hasValidValues(String[] array) {
        if (array == null || array.length == 0) {
            return false;
        }
        for (String value : array) {
            if (value == null || value.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.channelRegistrar.setApplicationContext(applicationContext);
        this.channelRegistrar.afterSingletonsInstantiated();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
