package io.github.jihongkim98.redisextension;

import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.listener.Topic;

public class ChannelRegistrar implements SmartInitializingSingleton {

    private final Log logger = LogFactory.getLog(ChannelRegistrar.class);

    private ApplicationContext applicationContext;

    public void registerChannel(Object delegate, Method method, Topic topic) {
        // TODO

    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        // TODO

    }
}
