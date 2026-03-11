package io.badgod;

import io.badgod.jayreq.*;

import org.junit.jupiter.api.Test;

import static io.badgod.jayreq.JayReq.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JayReqErrorTest extends HttpBinIntegrationTest {

    @Test
    void should_throw_on_connection_error() {
        var error = assertThrows(JayReq.Error.class, () -> get("http://localhost"));
        assertThat(error.request(), is(not(nullValue())));
        assertThat(error.request().method(), is(Method.GET));
        assertThat(error.response().isPresent(), is(false));
        assertThat(error.getMessage(), containsString("GET"));
        assertThat(error.getMessage(), containsString("http://localhost"));
    }

    @Test
    void should_create_error_with_response() {
        var request = new Request(testUrl("/anything"));
        var response = get(testUrl("/anything"));
        var cause = new RuntimeException(new IllegalStateException("test"));
        var error = new JayReq.Error(request, response, cause);
        assertThat(error.request(), is(request));
        assertThat(error.response().isPresent(), is(true));
        assertThat(error.response().get(), is(response));
        assertThat(error.getMessage(), containsString("RuntimeException"));
        assertThat(error.getMessage(), containsString("IllegalStateException"));
    }
}
