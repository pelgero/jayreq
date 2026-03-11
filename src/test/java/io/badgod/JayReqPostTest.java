package io.badgod;

import io.badgod.jayreq.*;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class JayReqPostTest extends HttpBinIntegrationTest {

    private final Gson gson = new Gson();
    private final Body.Converter<HttpBinResponse> converter = (s, h, b) -> gson.fromJson(b, HttpBinResponse.class);

    @Test
    void should_do_post() {
        Request req = new Request(testUrl("/anything"));
        var body = new JayReq.Client().post(req).body(converter);
        assertThat(body.isPresent(), is(true));
        assertThat(body.get().url(), is(testUrl("/anything")));
        assertThat(body.get().method(), is("POST"));
    }

    @Test
    void should_do_post_with_body() {
        var req = new Request(
            Method.POST,
            testUri("/anything"),
            Body.of("some-body"),
            Headers.of("X-Header1", "header-1-value")
        );
        var body = new JayReq.Client().post(req).body(converter);
        assertThat(body.isPresent(), is(true));
        assertThat(body.get().url(), is(testUrl("/anything")));
        assertThat(body.get().method(), is("POST"));
        assertThat(body.get().data(), is("some-body"));
    }
}
