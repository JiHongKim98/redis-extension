package io.github.jihongkim98.redisextensions;

import java.lang.reflect.Method;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogAccessor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.RedisSerializer;

public class BroadcastMethodInvoker implements MessageListener {

    private static final RedisSerializer<String> serializer = RedisSerializer.string();

    private final LogAccessor logger = new LogAccessor(LogFactory.getLog(getClass()));

    private final Object delegate;

    private final Method method;

    public BroadcastMethodInvoker(Object delegate, Method method) {
        this.delegate = delegate;

        // support private method
        if (!method.canAccess(delegate)) {
            method.setAccessible(true);
        }
        this.method = method;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        byte[] body = message.getBody();
        String payload = serializer.deserialize(body);

        try {
            method.invoke(delegate, payload);
        } catch (Throwable th) {
            logger.error(th, "Failed to invoke method '" + method.getName() +
                    "' on delegate '" + delegate.getClass().getName() + "'");
        }
    }
}
