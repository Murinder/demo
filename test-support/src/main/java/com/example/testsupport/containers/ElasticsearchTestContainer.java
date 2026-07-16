package com.example.testsupport.containers;

import org.testcontainers.elasticsearch.ElasticsearchContainer;

/**
 * Singleton Elasticsearch container shared across all integration tests.
 */
public final class ElasticsearchTestContainer {

    @SuppressWarnings("resource")
    private static final ElasticsearchContainer CONTAINER =
            new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.11.3")
                    .withEnv("xpack.security.enabled", "false")
                    .withEnv("discovery.type", "single-node")
                    .withReuse(true);

    static {
        CONTAINER.start();
    }

    private ElasticsearchTestContainer() {}

    public static ElasticsearchContainer getInstance() {
        return CONTAINER;
    }

    public static String getHttpHostAddress() {
        return CONTAINER.getHttpHostAddress();
    }
}
