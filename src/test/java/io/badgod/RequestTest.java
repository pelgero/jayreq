package io.badgod;

import io.badgod.jayreq.*;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class RequestTest {

    @Test
    void should_create_empty_headers_in_request() {
        assertThat(new Request("http://x", (Headers[]) null).headers(), is(Headers.empty()));
        assertThat(new Request("http://x", (Headers) null).headers(), is(Headers.empty()));
        assertThat(new Request("http://x").headers(), is(Headers.empty()));
        assertThat(new Request("http://x", Headers.of((String) null)).headers(), is(Headers.empty()));
        assertThat(new Request("http://x", Headers.of(null)).headers(), is(Headers.empty()));
    }

    @Test
    void should_merge_headers() {
        var req = new Request(
            "http://x",
            Headers.of("x", "1"),
            Headers.of("x", "2"),
            Headers.of("y", "3")
        );
        assertThat(req.headers().toStringArray(), is(new String[]{"x", "1,2", "y", "3"}));
    }

    @Test
    void should_expose_body() {
        var req = new Request(
            Method.POST,
            URI.create("http://x"),
            Body.of("hello"),
            Headers.of("X", "1")
        );
        assertThat(req.body().value().isPresent(), is(true));
        assertThat(req.body().value().get(), is("hello"));
    }

    @Test
    void should_have_no_body_for_simple_request() {
        var req = new Request("http://x");
        assertThat(req.body().isEmpty(), is(true));
    }

    @Test
    void should_default_to_get_method() {
        var req = new Request("http://x");
        assertThat(req.method(), is(Method.GET));
        assertThat(req.uri(), is(URI.create("http://x")));
    }

    @Test
    void should_have_string_representation() {
        var req = new Request(
            Method.POST,
            URI.create("http://example.com"),
            Body.of("test-body"),
            Headers.of("X-Foo", "bar")
        );
        var str = req.toString();
        assertThat(str, containsString("POST"));
        assertThat(str, containsString("http://example.com"));
        assertThat(str, containsString("test-body"));
    }
}
