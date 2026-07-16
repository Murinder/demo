package com.example.testsupport.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Singleton Redis container shared across all integration tests.
 */
public final class RedisTestContainer {

    private static final int REDIS_PORT = 6379;

    @SuppressWarnings("resource")
    private static final GenericContainer<?> CONTAINER =
            new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                    .withExposedPorts(REDIS_PORT)
                    .withReuse(true);

    static {
        CONTAINER.start();
    }

    private RedisTestContainer() {}

    public static GenericContainer<?> getInstance() {
        return CONTAINER;
    }

    public static String getHost() {
        return CONTAINER.getHost();
    }

    public static int getPort() {
        return CONTAINER.getMappedPort(REDIS_PORT);
    }
}
