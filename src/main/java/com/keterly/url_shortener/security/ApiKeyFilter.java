package com.keterly.url_shortener.security;

import com.keterly.url_shortener.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${security.api-key}")
    private String expectedApiKey;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/h2-console");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        boolean isProtectedEndpoint =
                "POST".equalsIgnoreCase(request.getMethod()) &&
                        "/api/v1/urls".equals(request.getRequestURI());

        if (!isProtectedEndpoint) {
            filterChain.doFilter(request, response);
            return;
        }

        String receivedApiKey = request.getHeader("X-API-Key");

        if (receivedApiKey == null || !receivedApiKey.equals(expectedApiKey)) {
            throw new UnauthorizedException("Missing or invalid X-API-Key");
        }

        filterChain.doFilter(request, response);
    }
}
