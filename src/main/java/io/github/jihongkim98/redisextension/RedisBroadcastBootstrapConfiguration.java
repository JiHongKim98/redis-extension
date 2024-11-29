package io.github.jihongkim98.redisextension;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class RedisBroadcastBootstrapConfiguration implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(BroadcastConfigUtils.REDIS_BROADCAST_ANNOTATION_BEAN_POST_PROCESSOR)) {

            registry.registerBeanDefinition(
                    BroadcastConfigUtils.REDIS_BROADCAST_ANNOTATION_BEAN_POST_PROCESSOR,
                    new RootBeanDefinition(RedisBroadcastAnnotationBeanPostProcessor.class)
            );
        }
    }
}
