package com.gym.crm.config;

import com.redis.testcontainers.RedisContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.utility.DockerImageName;

public final class RedisIntegrationTest {

    private static final RedisContainer REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new RedisContainer(DockerImageName.parse("redis:8.8.0"));
        REDIS_CONTAINER.start();
    }

    private RedisIntegrationTest() {
    }

    public static void setRedisContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getRedisHost);
        registry.add("spring.data.redis.port", REDIS_CONTAINER::getRedisPort);
    }

}
