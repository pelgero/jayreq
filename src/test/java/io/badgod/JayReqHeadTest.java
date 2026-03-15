package io.badgod;

import io.badgod.jayreq.*;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class JayReqHeadTest extends HttpBinIntegrationTest {

    @Test
    void should_do_head() {
        Request req = new Request(testUrl("/anything"));
        var resp = new JayReq.Client().head(req);
        assertThat(resp.status(), is(200));
        assertThat(resp.body().isEmpty(), is(true));
        assertThat(resp.headers().isPresent(), is(true));
    }
}
