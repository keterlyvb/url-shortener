package com.keterly.url_shortener.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class CreateUrlShortenerRequest {

    @JsonProperty("originalUrl")
    String originalUrl;

    @JsonProperty("expirationDate")
    LocalDateTime expirationDate;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }
}


