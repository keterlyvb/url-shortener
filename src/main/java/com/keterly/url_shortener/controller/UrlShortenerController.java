package com.keterly.url_shortener.controller;

import com.keterly.url_shortener.dto.request.CreateUrlShortenerRequest;
import com.keterly.url_shortener.dto.response.CreateUrlShortenerResponse;
import com.keterly.url_shortener.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
public class UrlShortenerController {

    @Autowired
    UrlShortenerService service;

    @PostMapping("/urls")
    public ResponseEntity<CreateUrlShortenerResponse> createUrlShortener(@RequestBody CreateUrlShortenerRequest request){

        CreateUrlShortenerResponse response = service.createUrl(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> getByShortUrl(@PathVariable String shortUrl){

        String originalUrl = service.getOriginalUrlByShortId(shortUrl);

        return ResponseEntity
                .status(302)
                .location(URI.create(originalUrl))
                .build();

    }
}
