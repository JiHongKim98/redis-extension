package io.github.jihongkim98.redisextension.aot;

import io.github.jihongkim98.redisextension.BroadcastMethodInvoker;
import io.github.jihongkim98.redisextension.RedisBroadcastAnnotationBeanPostProcessor;
import io.github.jihongkim98.redisextension.annotation.RedisBroadcastListener;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class RedisBroadcastListenerHintsRegistrar implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(BroadcastMethodInvoker.class, builder ->
                builder.withMembers(
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_PUBLIC_METHODS
                )
        );

        hints.reflection().registerType(RedisBroadcastAnnotationBeanPostProcessor.class, builder ->
                builder.withMembers(
                        MemberCategory.INVOKE_PUBLIC_METHODS,
                        MemberCategory.INVOKE_DECLARED_METHODS
                )
        );

        hints.proxies().registerJdkProxy(RedisBroadcastListener.class);
    }
}
