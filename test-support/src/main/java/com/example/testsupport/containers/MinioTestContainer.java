package com.example.testsupport.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Singleton MinIO container shared across all integration tests.
 */
public final class MinioTestContainer {

    private static final int MINIO_PORT = 9000;

    @SuppressWarnings("resource")
    private static final GenericContainer<?> CONTAINER =
            new GenericContainer<>(DockerImageName.parse("minio/minio:latest"))
                    .withExposedPorts(MINIO_PORT)
                    .withEnv("MINIO_ROOT_USER", "minioadmin")
                    .withEnv("MINIO_ROOT_PASSWORD", "minioadmin")
                    .withCommand("server /data")
                    .withReuse(true);

    static {
        CONTAINER.start();
    }

    private MinioTestContainer() {}

    public static GenericContainer<?> getInstance() {
        return CONTAINER;
    }

    public static String getUrl() {
        return "http://" + CONTAINER.getHost() + ":" + CONTAINER.getMappedPort(MINIO_PORT);
    }

    public static String getAccessKey() {
        return "minioadmin";
    }

    public static String getSecretKey() {
        return "minioadmin";
    }
}
