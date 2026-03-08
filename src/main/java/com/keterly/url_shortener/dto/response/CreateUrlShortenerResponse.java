package com.keterly.url_shortener.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class CreateUrlShortenerResponse {
    public CreateUrlShortenerResponse(String UrlId, String shortUrl, String originalUrl, LocalDateTime createdAt, LocalDateTime expirationDate) {
        this.UrlId = UrlId;
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
        this.expirationDate = expirationDate;
    }

    @JsonProperty("id")
    private String UrlId;

    @JsonProperty("shortUrl")
    private String shortUrl;

    @JsonProperty("originalUrl")
    private String originalUrl;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("expirationDate")
    private LocalDateTime expirationDate;

}
