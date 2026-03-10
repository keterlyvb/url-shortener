package com.keterly.url_shortener.integration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "url-shortener.base-url=http://localhost:8080/",
        "url-shortener.obfuscation-secret=12345"})
class UrlShortenerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateShortUrlSuccessfully() throws Exception {

        String payload = """
                {
                  "originalUrl": "https://www.itau.com.br",
                  "expirationDate": "2026-12-31T23:59:59"
                }
                """;

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.shortUrl", notNullValue()))
                .andExpect(jsonPath("$.originalUrl").value("https://www.itau.com.br"));
    }
}