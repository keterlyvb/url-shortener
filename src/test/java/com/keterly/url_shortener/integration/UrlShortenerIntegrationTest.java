package com.keterly.url_shortener.integration;

import org.junit.jupter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.context.WebApplicationContext;



import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource(properties = {
        "url-shortener.base-url=http://localhost:8080/",
        "url-shortener.obfuscation-secret=12345",
        "security.api-key=test-api-key"})
class UrlShortenerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

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
                        .header("X-API-key", "test-api-key")
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.shortUrl", notNullValue()))
                .andExpect(jsonPath("$.originalUrl").value("https://www.itau.com.br"));
    }
}