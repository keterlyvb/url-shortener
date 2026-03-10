package com.keterly.url_shortener.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UrlDetailsResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("shortUrl")
    private String shortUrl;

    @JsonProperty("originalUrl")
    private String originalUrl;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("expirationDate")
    private LocalDateTime expirationDate;

    @JsonProperty("clickCount")
    private Long clickCount;
}
