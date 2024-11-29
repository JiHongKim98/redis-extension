package io.github.jihongkim98.redisextension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.core.log.LogAccessor;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;

public class TopicRegistrar implements SmartInitializingSingleton {

    private final LogAccessor logger = new LogAccessor(LogFactory.getLog(getClass()));

    private final Map<BroadcastMethodInvoker, List<Topic>> invokers = new ConcurrentHashMap<>();

    private ApplicationContext applicationContext;

    private RedisMessageListenerContainer container;

    public void registry(Object delegate, Method method, Topic topic) {
        BroadcastMethodInvoker invoker = new BroadcastMethodInvoker(delegate, method);
        invokers.computeIfAbsent(invoker, v -> new ArrayList<>()).add(topic);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        try {
            this.container = applicationContext.getBean(RedisMessageListenerContainer.class);
        } catch (BeansException e) {
            logger.error("RedisMessageListenerContainer bean is missing in the application context. " +
                    "Please register it as a @Bean in your configuration class to enable Redis Pub/Sub functionality.");
            throw e;
        }
        containerRegistry();
        this.container.start();
    }

    private void containerRegistry() {
        invokers.forEach((invoker, topics) -> {
            this.container.addMessageListener(invoker, topics);
        });
    }
}
