package io.badgod;

import io.badgod.jayreq.*;

import org.junit.jupiter.api.Test;

import static io.badgod.jayreq.Headers.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RequestTest {

    @Test
    void should_create_empty_headers_in_request() {
        assertThat(new Request("http://x", (Headers[]) null).headers(), is(Headers.empty()));
        assertThat(new Request("http://x", (Headers) null).headers(), is(Headers.empty()));
        assertThat(new Request("http://x").headers(), is(Headers.empty()));
        assertThat(new Request("http://x", of((String) null)).headers(), is(Headers.empty()));
        assertThat(new Request("http://x", of(null)).headers(), is(Headers.empty()));
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
}
