package io.badgod;

import io.badgod.jayreq.*;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class JayReqDeleteTest extends TestContainerIntegrationTest {

    private final Gson gson = new Gson();
    private final Body.Converter<HttpBinDeleteResponse> converter = (s, h, b) -> gson.fromJson(b, HttpBinDeleteResponse.class);

    @Test
    void should_do_delete() {
        Request req = new Request(testUrl("/anything"));
        var body = new JayReq.Client().delete(req).body(converter);
        assertThat(body.isPresent(), is(true));
        assertThat(body.get().url(), is(testUrl("/anything")));
        assertThat(body.get().method(), is("DELETE"));
    }

    private record HttpBinDeleteResponse(String url, String method, String data, String json) {}
}
