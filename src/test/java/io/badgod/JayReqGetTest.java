package io.badgod;

import io.badgod.jayreq.*;

import com.google.gson.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.badgod.jayreq.JayReq.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JayReqGetTest extends HttpBinIntegrationTest {

    private static final Gson gson = new Gson();
    private final Body.Converter<HttpBinResponse> converter = (s, h, b) -> gson.fromJson(b, HttpBinResponse.class);

    @Test
    void should_do_get_request_via_shortcut() {
        var resp = get(testUrl("/anything"));
        var body = resp.body(converter);
        assertThat(body.isPresent(), is(true));
        assertThat(body.get().url(), is(testUrl("/anything")));
        assertThat(body.get().method(), is("GET"));
    }

    @Test
    void should_do_get_request_via_instance() {
        JayReq jr = new JayReq.Client();
        Optional<HttpBinResponse> body = jr.get(new Request(testUrl("/anything"))).body(converter);
        assertThat(body.isPresent(), is(true));
        assertThat(body.get().url(), is(testUrl("/anything")));
        assertThat(body.get().method(), is("GET"));
    }

    @Test
    void should_contain_response_headers() {
        var resp = get(testUrl("/headers"));
        assertThat(resp.headers().isPresent(), is(true));
        // access to headers (i.e. the header keys) is case-insensitive!
        assertThat(resp.headers().get("Content-Type").isPresent(), is(true));
        assertThat(resp.headers().get("Content-Type").get(), is(List.of("application/json")));
        assertThat(resp.headers().get("CONTENT-TYPE").get(), is(List.of("application/json")));
        assertThat(resp.headers().get("content-type").get(), is(List.of("application/json")));
    }

    @Test
    void should_contain_empty_body_when_there_is_no_body_in_http_response() {
        var resp = get(testUrl("/status/200"));
        assertThat(resp.body().isEmpty(), is(true));
        assertThat(resp.headers().isPresent(), is(true));
    }

    @Test
    void should_send_request_headers() {
        var resp = get(
            testUrl("/headers"),
            Headers.of("Authorization", "Bearer xyz"),
            Headers.of("X-Test", "Hello"),
            Headers.of("X-Test", "World!")
        );
        // The request headers are returned in the body in field "headers"
        // see: https://httpbin.org/#/Request_inspection/get_headers
        var body = resp.body((s, h, b) -> new Gson().fromJson(b, HttpBinHeadersResponse.class));
        assertThat(body.isPresent(), is(true));
        assertThat(body.get().headers().entrySet(), is(not(empty())));
        assertThat(body.get().headers().get("Authorization"), is("Bearer xyz"));
        assertThat(body.get().headers().get("X-Test"), is("Hello,World!"));
    }

    @Test
    void should_contain_status_in_response() {
        assertThat(JayReq.get(testUrl("/status/200")).status(), is(200));
        assertThat(JayReq.get(testUrl("/status/503")).status(), is(503));
        assertThat(JayReq.get(testUrl("/status/429")).status(), is(429));
        assertThat(JayReq.get(testUrl("/status/302101")).status(), is(302));
    }

    @Test
    void should_throw_on_failed_json_mapping() {
        String url = testUrl("/anything");
        assertThrows(
            JsonSyntaxException.class,
            () -> get(url).body((s, h, b) -> gson.fromJson(b, InvalidHttpBinResponse.class)));
    }

    private record HttpBinHeadersResponse(Map<String, String> headers) {}
}
