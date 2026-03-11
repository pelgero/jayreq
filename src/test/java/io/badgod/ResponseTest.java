package io.badgod;

import io.badgod.jayreq.*;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ResponseTest {

    @Test
    void should_contain_request() {
        var request = new Request("http://example.com", Headers.of("X", "123"));
        var response = new Response(request, "{}", 200, Map.of());
        assertThat(response.request(), is(request));
    }

    @Test
    void should_expose_status() {
        var response = new Response(new Request("http://x"), "", 404, Map.of());
        assertThat(response.status(), is(404));
    }

    @Test
    void should_expose_headers() {
        var response = new Response(
            new Request("http://x"), "", 200,
            Map.of("Content-Type", List.of("application/json"))
        );
        assertThat(response.headers().isPresent(), is(true));
        assertThat(response.headers().get("Content-Type").isPresent(), is(true));
        assertThat(response.headers().get("Content-Type").get(), is(List.of("application/json")));
    }

    @Test
    void should_expose_body() {
        var response = new Response(new Request("http://x"), "hello", 200, Map.of());
        assertThat(response.body().value().isPresent(), is(true));
        assertThat(response.body().value().get(), is("hello"));
    }

    @Test
    void should_have_empty_body_when_response_is_blank() {
        var response = new Response(new Request("http://x"), "", 200, Map.of());
        assertThat(response.body().isEmpty(), is(true));
    }

    @Test
    void should_convert_body_with_converter() {
        var response = new Response(new Request("http://x"), "raw-body", 200, Map.of());
        var converted = response.body((status, headers, body) -> body.toUpperCase());
        assertThat(converted.isPresent(), is(true));
        assertThat(converted.get(), is("RAW-BODY"));
    }

    @Test
    void should_return_empty_when_converting_empty_body() {
        var response = new Response(new Request("http://x"), "", 200, Map.of());
        var converted = response.body((status, headers, body) -> body.toUpperCase());
        assertThat(converted.isPresent(), is(false));
    }

    @Test
    void should_have_string_representation() {
        var response = new Response(new Request("http://x"), "test-body", 201, Map.of());
        var str = response.toString();
        assertThat(str, containsString("201"));
        assertThat(str, containsString("test-body"));
    }
}
