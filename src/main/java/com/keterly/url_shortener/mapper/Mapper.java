package com.keterly.url_shortener.mapper;

import com.keterly.url_shortener.dto.response.CreateUrlShortenerResponse;
import com.keterly.url_shortener.entity.UrlEntity;

public class Mapper {

    public static CreateUrlShortenerResponse entityToResponse(UrlEntity entity, String baseUrl){

        return CreateUrlShortenerResponse.builder()
                .UrlId(entity.getShortUrl())
                .shortUrl(baseUrl + entity.getShortUrl())
                .originalUrl(entity.getOriginalUrl())
                .createdAt(entity.getCreatedAt())
                .expirationDate(entity.getExpirationDate())
                .build();
    }
}
