package com.keterly.url_shortener.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class CreateUrlShortenerRequest {

    @NotBlank(message = "originalUrl must not be null or blank")
    @Pattern(
            regexp = "^(https?://).+",
            message = "originalUrl must be a valid URL starting with http:// or https://"
    )
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


