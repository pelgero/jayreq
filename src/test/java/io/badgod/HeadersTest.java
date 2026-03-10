package io.badgod;

import io.badgod.jayreq.Headers;

import org.junit.jupiter.api.Test;

import java.util.*;

import static io.badgod.jayreq.Headers.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HeadersTest {

    @Test
    void should_have_headers_present() {
        assertThat(of("x", "y").isPresent(), is(true));
        assertThat(Headers.empty().isPresent(), is(false));
    }

    @Test
    void should_throw_if_no_header_values() {
        assertThrows(IllegalArgumentException.class, () -> of("x"));
    }

    @Test
    void should_merge_headers() {
        var merged = Headers.mergeAll(
            of("X", "value1"),
            of("y", "value2"),
            of("x", "value3")
        );
        assertThat(
            merged.toStringArray(),
            is(new String[]{"X", "value1,value3", "y", "value2"}));
    }

    @Test
    void should_flatten_to_array() {
        var arr = of("X-Test", "a", "b").toStringArray();
        assertThat(arr.length, is(2));
        assertThat(arr[0], is("X-Test"));
        assertThat(arr[1], is("a,b"));
    }

    @Test
    void should_throw_when_request_headers_not_in_pairs() {
        assertThrows(IllegalArgumentException.class, () -> of("X-Test"));
    }

    @Test
    void should_create_headers_from_map() {
        var map = new TreeMap<String, List<String>>();
        map.put("x", List.of("a"));
        map.put("y", List.of("b", "c"));
        var headers = of(map);
        assertThat(headers.isPresent(), is(true));
        assertThat(headers.toStringArray(), is(new String[]{"x", "a", "y", "b,c"}));
    }

    @Test
    void should_get_header_value() {
        var headers = of(Map.of(
            "x", List.of("a", "b"),
            "y", List.of("C")
        ));
        assertThat(headers.get("x").isPresent(), is(true));
        assertThat(headers.get("X").isPresent(), is(true));
        assertThat(headers.get("y").isPresent(), is(true));
        assertThat(headers.get("z").isPresent(), is(false));
        assertThat(headers.get("x").get(), is(List.of("a", "b")));
        assertThat(headers.get("y").get(), is(List.of("C")));
    }

    @Test
    void should_create_auth_bearer_header() {
        assertThat(Headers.authBearer("some-token-value").toStringArray()[0], is("Authorization"));
        assertThat(Headers.authBearer("some-token-value").toStringArray()[1], is("Bearer some-token-value"));
    }

    @Test
    void should_set_valid_auth_basic_header() {
        assertThat(Headers.authBasic("Guy", "Test").toStringArray()[0], is("Authorization"));
        assertThat(Headers.authBasic("Guy", "Test").toStringArray()[1], is("Basic R3V5OlRlc3Q="));
        assertThat(Headers.authBasic(null, "Test").toStringArray()[1], is("Basic OlRlc3Q="));
        assertThat(Headers.authBasic("Guy", null).toStringArray()[1], is("Basic R3V5Og=="));
        assertThat(Headers.authBasic(null, null).toStringArray()[1], is("Basic Og=="));
    }

    @Test
    void should_set_accept_headers() {
        var acceptHeader = Headers.accept("application/json", "text/plain");
        assertThat(acceptHeader.toStringArray()[0], is("Accept"));
        assertThat(acceptHeader.toStringArray()[1], is("application/json,text/plain"));
    }
}
