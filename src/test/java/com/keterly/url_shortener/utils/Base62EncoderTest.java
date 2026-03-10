package com.keterly.url_shortener.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {

    @Test
    void shouldEncodeAndDecodeSuccessfully() {
        long original = 12345L;

        String encoded = Base62Encoder.encode(original);
        long decoded = Base62Encoder.decode(encoded);

        assertEquals(original, decoded);
    }

    @Test
    void shouldEncodeZero() {
        assertEquals("0", Base62Encoder.encode(0));
    }

    @Test
    void shouldThrowExceptionForInvalidCharacter() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Base62Encoder.decode("@@@"));

        assertTrue(ex.getMessage().contains("Invalid Base62 character"));
    }
}