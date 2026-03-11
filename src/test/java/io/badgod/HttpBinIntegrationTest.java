package io.badgod;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;

import java.net.URI;
import java.util.Map;

public abstract class HttpBinIntegrationTest {

    protected static GenericContainer<?> container;

    protected record HttpBinResponse(String url, String method, String data, String json, Map<String, String> headers) {}
    protected record InvalidHttpBinResponse(int url, int method) {}

    @BeforeAll
    public static void startTestContainer() {
        try (var c = new GenericContainer<>("kennethreitz/httpbin:latest")) {
            container = c.withExposedPorts(80);
        }
        container.start();
    }

    @AfterAll
    public static void stopTestContainer() {
        container.stop();
        container.close();
    }

    protected static String testUrl(String path) {
        return String.format("http://%s:%s%s",
            container.getHost(),
            container.getFirstMappedPort(),
            path);
    }

    protected static URI testUri(String path) {
        return URI.create(testUrl(path));
    }
}
