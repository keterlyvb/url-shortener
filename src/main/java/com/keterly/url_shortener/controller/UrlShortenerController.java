package com.keterly.url_shortener.controller;

import com.keterly.url_shortener.dto.request.CreateUrlShortenerRequest;
import com.keterly.url_shortener.dto.response.CreateUrlShortenerResponse;
import com.keterly.url_shortener.dto.response.UrlDetailsResponse;
import com.keterly.url_shortener.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
public class UrlShortenerController {

    @Autowired
    UrlShortenerService service;

    @PostMapping("/urls")
    public ResponseEntity<CreateUrlShortenerResponse> createUrlShortener(@Valid @RequestBody CreateUrlShortenerRequest request){

        CreateUrlShortenerResponse response = service.createUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode){

        String originalUrl = service.getOriginalUrlByShortCode(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();

    }

    @GetMapping("/urls/{id}")
    public ResponseEntity<UrlDetailsResponse> getUrlDetails(@PathVariable String id) {
        return ResponseEntity.ok(service.getUrlDetails(id));
    }

    @GetMapping("/urls")
    public ResponseEntity<Page<UrlDetailsResponse>> listUrls(Pageable pageable) {
        return ResponseEntity.ok(service.listUrls(pageable));
    }

}
