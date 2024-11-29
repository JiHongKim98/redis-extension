package io.github.jihongkim98.redisextension.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisBroadcastListener {

    String[] channels() default {};

    String[] patterns() default {};
}
