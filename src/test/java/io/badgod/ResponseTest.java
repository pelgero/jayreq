package io.badgod;

import io.badgod.jayreq.*;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ResponseTest {

    @Test
    void should_contain_request() {
        var request = new Request("http://example.com", Headers.of("X", "123"));
        var response = new Response(request, "{}", 200, Map.of());
        assertThat(response.request(), is(request));
    }
}
