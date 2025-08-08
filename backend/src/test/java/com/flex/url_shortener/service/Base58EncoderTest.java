package com.flex.url_shortener.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.flex.url_shortener.common.Base58Encoder;
import org.junit.jupiter.api.Test;

public class Base58EncoderTest {
    private static final long NUMBER_TO_ENCODE = 1_000_000_000L;
    private static final String EXPECTED_BASE58_RESULT = "2XNGAK";

    @Test
    public void encodeBase58_shouldReturnExpectedString() {
        var encodedString = Base58Encoder.encodeBase58(NUMBER_TO_ENCODE);
        assertEquals(EXPECTED_BASE58_RESULT, encodedString);
    }

}
