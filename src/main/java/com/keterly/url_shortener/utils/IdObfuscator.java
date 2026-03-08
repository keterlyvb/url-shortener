package com.keterly.url_shortener.utils;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

@Service
public class IdObfuscator {

    @Value("${url-shortener.obfuscation-secret}")
    private Long secret;

    public long obfuscate(long id) {
        return id ^ secret;
    }

    public long desobfuscate(long obfuscatedId) {
        return obfuscatedId ^ secret;
    }

}
