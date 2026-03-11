package com.keterly.url_shortener.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IdObfuscatorTest {

    private IdObfuscator idObfuscator;

    @BeforeEach
    void setUp() {
        idObfuscator = new IdObfuscator();
        ReflectionTestUtils.setField(idObfuscator, "secret", 123456L);
    }

    @Test
    void shouldObfuscateIdCorrectly() {
        long result = idObfuscator.obfuscate(1L);

        assertEquals(123457L, result);
    }

    @Test
    void shouldDesobfuscateIdCorrectly() {
        long result = idObfuscator.desobfuscate(123457L);

        assertEquals(1L, result);
    }

    @Test
    void shouldReturnOriginalIdAfterObfuscateAndDesobfuscate() {
        long originalId = 42L;

        long obfuscatedId = idObfuscator.obfuscate(originalId);
        long restoredId = idObfuscator.desobfuscate(obfuscatedId);

        assertEquals(originalId, restoredId);
    }
}