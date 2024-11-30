# Redis Extensions <img src="https://github.com/user-attachments/assets/0f4a3bef-e476-457b-9131-77fb45939c52?s=200&v=4" height = 100 align = left>

> A lightweight annotation-based Redis Pub/Sub library for simplified subscription handling.

![version 0.0.1](https://img.shields.io/badge/version-0.0.1-red?labelColor=black&style=flat-square) ![minimum_jdk](https://img.shields.io/badge/minimum_jdk-17-white?labelColor=black&style=flat-square)

## Dependency

Ensure you have added the Redis Extensions dependency to your `pom.xml` (Maven) or `build.gradle` (Gradle).

**Maven:**

```xml
<!-- pom.xml -->

<dependency>
    <groupId>io.github.jihongkim98</groupId>
    <artifactId>redis-extensions</artifactId>
    <version>0.0.1</version>
</dependency>
```

**Gradle:**

```groovy
// build.gradle

dependencies {
    implementation 'io.github.jihongkim98:redis-extensions:0.0.1'
}
```

See [maven.org](https://central.sonatype.com/artifact/io.github.jihongkim98/redis-extensions/overview)

## Usage

To use **Redis Extensions** in your Spring project, follow these steps:

### Configure RedisMessageListenerContainer

You must register a `RedisMessageListenerContainer` as a Spring Bean in your configuration class.
This is required to enable Redis Pub/Sub message processing.

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    // ...
}
```

### Enable Redis Extensions

Use the `@EnableRedisBroadcast` annotation in your configuration class to activate the Redis Extensions library.

```java
import io.github.jihongkim98.redisextensions.EnableRedisBroadcast;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRedisBroadcast
public class RedisExtensionConfig {
}
```

### Example Usage

After completing the configuration,
you can define methods annotated with `@RedisBroadcastListener` in your beans to handle Redis Pub/Sub messages.

```java
import io.github.jihongkim98.redisextensions.annotation.RedisBroadcastListener;
import org.springframework.stereotype.Service;

@Service
public class SomeHandler {

    @RedisBroadcastListener(channels = "channel:example")
    public void listener(String message) {
        System.out.println("Received message: " + message);
    }
}
```

The above example demonstrates how you can use the `@RedisBroadcastListener` annotation to listen to messages published to a specific Redis channel (`channel:example`).

Below is an example of how to **publish messages** using Redis Pub/Sub to test the `SomeHandler` class

```java
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SomeBroadcastHandler {

    private final StringRedisTemplate redisTemplate;

    public void execute() {
        redisTemplate.convertAndSend(
                "channel:example",  // channel name
                "Hello World!"      // message to be sent
        );
    }
}
```

To test the functionality:

1. Execute the `execute()` method from the `SomeBroadcastHandler` class.
   This method sends a message (`"Hello World!"`) to the Redis channel named `"channel:example"`.
2. If everything is configured correctly, the `listener` method in the `SomeHandler` class will be triggered
   automatically.
3. You should see the following output in the console:
   ```
   Received message: Hello World!
   ```
   This confirms that the message was successfully published and received through the Redis Pub/Sub system.
