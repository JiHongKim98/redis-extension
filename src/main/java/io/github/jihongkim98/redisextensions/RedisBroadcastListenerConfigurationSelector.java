package io.github.jihongkim98.redisextensions;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;

@Order
public class RedisBroadcastListenerConfigurationSelector implements DeferredImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{RedisBroadcastBootstrapConfiguration.class.getName()};
    }
}
