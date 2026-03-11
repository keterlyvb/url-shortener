package com.keterly.url_shortener.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Base62EncoderTest {

    @Test
    void shouldEncodeLongToBase62Correctly() {
        String result = Base62Encoder.encode(123457L);

        assertEquals("W7F", result);
    }

    @Test
    void shouldDecodeBase62ToLongCorrectly() {
        long result = Base62Encoder.decode("W7F");

        assertEquals(123457L, result);
    }

    @Test
    void shouldReturnZeroWhenEncodingZero() {
        String result = Base62Encoder.encode(0L);

        assertEquals("0", result);
    }

    @Test
    void shouldReturnOriginalValueAfterEncodeAndDecode() {
        long originalValue = 999999L;

        String encoded = Base62Encoder.encode(originalValue);
        long decoded = Base62Encoder.decode(encoded);

        assertEquals(originalValue, decoded);
    }

    @Test
    void shouldThrowExceptionWhenDecodingInvalidCharacter() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Base62Encoder.decode("W7#")
        );

        assertEquals("Invalid Base62 character: #", exception.getMessage());
    }
}