package uk.gov.hmcts.befta.auth;

import org.junit.jupiter.api.Test;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormUrlEncodedExpanderTest {

    private final FormUrlEncodedExpander expander = new FormUrlEncodedExpander();

    @Test
    void shouldEncodeLiteralPlusCharacter() {
        String encoded = expander.expand("scope+value");

        assertEquals("scope%2Bvalue", encoded);
        assertEquals("scope+value", URLDecoder.decode(encoded, StandardCharsets.UTF_8));
    }

    @Test
    void shouldNotEncodeNull() {
        String encoded = expander.expand(null);

        assertEquals(null, encoded);
    }
}