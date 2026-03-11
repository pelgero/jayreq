package io.badgod;

import io.badgod.jayreq.Body;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class BodyTest {

    @Test
    void should_create_body_with_value() {
        var body = Body.of("hello");
        assertThat(body.value().isPresent(), is(true));
        assertThat(body.value().get(), is("hello"));
        assertThat(body.isEmpty(), is(false));
    }

    @Test
    void should_create_empty_body_from_null() {
        var body = Body.of(null);
        assertThat(body.value().isPresent(), is(false));
        assertThat(body.isEmpty(), is(true));
    }

    @Test
    void should_create_empty_body_from_blank_string() {
        assertThat(Body.of("").isEmpty(), is(true));
        assertThat(Body.of("   ").isEmpty(), is(true));
    }

    @Test
    void should_create_empty_body_with_none() {
        var body = Body.none();
        assertThat(body.value().isPresent(), is(false));
        assertThat(body.isEmpty(), is(true));
    }

    @Test
    void should_have_string_representation() {
        assertThat(Body.of("test").toString(), is("test"));
        assertThat(Body.none().toString(), is(nullValue()));
    }
}
