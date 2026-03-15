package io.badgod;

import io.badgod.jayreq.*;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class JayReqOptionsTest extends HttpBinIntegrationTest {

    @Test
    void should_do_options() {
        Request req = new Request(testUrl("/anything"));
        var resp = new JayReq.Client().options(req);
        assertThat(resp.status(), is(200));
        assertThat(resp.headers().isPresent(), is(true));
        assertThat(resp.headers().get("Allow").isPresent(), is(true));
    }
}
