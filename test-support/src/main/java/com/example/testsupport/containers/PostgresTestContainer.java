package com.example.testsupport.containers;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Singleton PostgreSQL container shared across all integration tests.
 * Reuses a single container to avoid startup overhead per test class.
 */
public final class PostgresTestContainer {

    private static final PostgreSQLContainer<?> CONTAINER =
            new PostgreSQLContainer<>("postgres:14-alpine")
                    .withDatabaseName("etsopy_test")
                    .withUsername("test")
                    .withPassword("test")
                    .withReuse(true);

    static {
        CONTAINER.start();
    }

    private PostgresTestContainer() {}

    public static PostgreSQLContainer<?> getInstance() {
        return CONTAINER;
    }

    public static String getJdbcUrl() {
        return CONTAINER.getJdbcUrl();
    }

    public static String getUsername() {
        return CONTAINER.getUsername();
    }

    public static String getPassword() {
        return CONTAINER.getPassword();
    }
}
