package io.badgod;

import io.badgod.jayreq.*;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class JayReqTraceTest extends HttpBinIntegrationTest {

    private final Gson gson = new Gson();
    private final Body.Converter<HttpBinResponse> converter = (s, h, b) -> gson.fromJson(b, HttpBinResponse.class);

    @Test
    void should_do_trace() {
        Request req = new Request(testUrl("/anything"));
        var resp = new JayReq.Client().trace(req);
        assertThat(resp.status(), is(200));
        var body = resp.body(converter);
        assertThat(body.isPresent(), is(true));
        assertThat(body.get().method(), is("TRACE"));
    }
}
