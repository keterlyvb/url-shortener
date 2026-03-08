package com.keterly.url_shortener.service;

import com.keterly.url_shortener.dto.request.CreateUrlShortenerRequest;
import com.keterly.url_shortener.dto.response.CreateUrlShortenerResponse;
import com.keterly.url_shortener.entity.UrlEntity;
import com.keterly.url_shortener.mapper.Mapper;
import com.keterly.url_shortener.repository.UrlShortenerRepository;
import com.keterly.url_shortener.utils.IdObfuscator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import static com.keterly.url_shortener.utils.Base62Encoder.decode;
import static com.keterly.url_shortener.utils.Base62Encoder.encode;


@Service
public class UrlShortenerService {

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
                .createdAt(LocalDateTime.now()) // verificar como retirar os milli segundos
                .build();

        UrlEntity savedUrl = repository.save(entity);

        long obfuscatedId = idObfuscator.obfuscate(savedUrl.getId());
        String shortUrl = encode(obfuscatedId);

        savedUrl.setShortUrl(shortUrl);
        UrlEntity finalUrl  = repository.save(savedUrl);

        return Mapper.entityToResponse(finalUrl, baseUrl);
    }

    public String getOriginalUrlByShortId(String shortId){

        long decodedValue = decode(shortId);
        long originalId = idObfuscator.desobfuscate(decodedValue);

        UrlEntity entity = repository.findById(originalId)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));

        if (entity.getExpirationDate() != null &&
                entity.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Short URL expired");
        }

        entity.setClickCount(entity.getClickCount() + 1);
        repository.save(entity);

        return entity.getOriginalUrl();

    }
}
