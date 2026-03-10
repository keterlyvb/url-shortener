package com.keterly.url_shortener.service;

import com.keterly.url_shortener.dto.request.CreateUrlShortenerRequest;
import com.keterly.url_shortener.dto.response.CreateUrlShortenerResponse;
import com.keterly.url_shortener.dto.response.UrlDetailsResponse;
import com.keterly.url_shortener.entity.UrlEntity;
import com.keterly.url_shortener.exception.UrlExpiredException;
import com.keterly.url_shortener.exception.UrlNotFoundException;
import com.keterly.url_shortener.mapper.Mapper;
import com.keterly.url_shortener.repository.UrlShortenerRepository;
import com.keterly.url_shortener.utils.IdObfuscator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import org.slf4j.Logger;

import static com.keterly.url_shortener.utils.Base62Encoder.decode;
import static com.keterly.url_shortener.utils.Base62Encoder.encode;


@Service
public class UrlShortenerService {

    private static final Logger log = LoggerFactory.getLogger(UrlShortenerService.class);

    @Value("${url-shortener.base-url}")
    private String baseUrl;
    private final UrlShortenerRepository repository;
    private final IdObfuscator idObfuscator;

    public UrlShortenerService(UrlShortenerRepository repository,
                               IdObfuscator idObfuscator) {
        this.repository = repository;
        this.idObfuscator = idObfuscator;
    }

    public CreateUrlShortenerResponse createUrl(CreateUrlShortenerRequest request){
        UrlEntity entity = UrlEntity.builder()
                .originalUrl(request.getOriginalUrl())
                .expirationDate(request.getExpirationDate())
                .createdAt(LocalDateTime.now().withNano(0))
                .build();

        UrlEntity savedUrl = repository.save(entity);

        long obfuscatedId = idObfuscator.obfuscate(savedUrl.getId());
        String shortUrl = encode(obfuscatedId);

        savedUrl.setShortUrl(shortUrl);
        UrlEntity finalUrl  = repository.save(savedUrl);

        log.info("Short URL created successfully. shortCode={}, originalUrl={}",
                finalUrl.getShortUrl(), finalUrl.getOriginalUrl());

        return Mapper.entityToResponse(finalUrl, baseUrl);
    }

    public String getOriginalUrlByShortCode(String shortCode){

        long decodedValue = decode(shortCode);
        long originalId = idObfuscator.desobfuscate(decodedValue);

        UrlEntity entity = repository.findById(originalId)
                .orElseThrow(() -> {
                    log.warn("Attempt to access non-existent shortCode={}", shortCode);
                    return new UrlNotFoundException("Short URL not found: " + shortCode);
                });

        validateExpiration(entity, shortCode);

        entity.setClickCount(entity.getClickCount() + 1);
        repository.save(entity);

        return entity.getOriginalUrl();

    }

    public UrlDetailsResponse getUrlDetails(String id) {
        UrlEntity entity = repository.findByShortUrl(id)
                .orElseThrow(() -> {
                    log.warn("Attempt to get details of non-existent shortCode={}", id);
                    return new UrlNotFoundException("Short URL not found: " + id);
                });

        return Mapper.entityToDetailsResponse(entity, baseUrl);
    }

    public Page<UrlDetailsResponse> listUrls(Pageable pageable) {
        return repository.findAll(pageable)
                .map(entity -> Mapper.entityToDetailsResponse(entity, baseUrl));
    }

    private void validateExpiration(UrlEntity entity, String shortId) {
        if (entity.getExpirationDate() != null &&
                entity.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException("Short URL expired: " + shortId);
        }
    }
}

