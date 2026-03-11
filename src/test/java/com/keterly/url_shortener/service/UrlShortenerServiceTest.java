package com.keterly.url_shortener.service;

import com.keterly.url_shortener.dto.request.CreateUrlShortenerRequest;
import com.keterly.url_shortener.entity.UrlEntity;
import com.keterly.url_shortener.repository.UrlShortenerRepository;
import com.keterly.url_shortener.utils.IdObfuscator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlShortenerServiceTest {

    @Mock
    private UrlShortenerRepository repository;
    @InjectMocks
    private UrlShortenerService service;
    @Mock
    private IdObfuscator idObfuscator;


    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(idObfuscator, "secret", 123456L);
        ReflectionTestUtils.setField(service, "idObfuscator", idObfuscator);
        ReflectionTestUtils.setField(service, "baseUrl", "http://localhost:8080");

    }

    @Test
    void shouldCreateShortUrlSuccessfully() throws Exception {
        LocalDateTime expirationDate = LocalDateTime.of(2026, 12, 31, 23, 59, 59);

        ReflectionTestUtils.setField(idObfuscator, "secret", 123456L);
        ReflectionTestUtils.setField(service, "baseUrl", "http://localhost:8080/");

        CreateUrlShortenerRequest request = new CreateUrlShortenerRequest();
        setField(request, "originalUrl", "https://google.com");
        setField(request, "expirationDate", LocalDateTime.of(2026, 12, 31, 23, 59, 59));

        UrlEntity firstSave = UrlEntity.builder()
                .id(1L)
                .originalUrl("https://google.com")
                .expirationDate(request.getExpirationDate())
                .createdAt(LocalDateTime.now())
                .clickCount(0L)
                .build();

        UrlEntity secondSave = UrlEntity.builder()
                .id(1L)
                .originalUrl("https://google.com")
                .shortUrl("W7H")
                .expirationDate(request.getExpirationDate())
                .clickCount(0L)
                .build();

        when(repository.save(any(UrlEntity.class)))
                .thenReturn(firstSave)
                .thenReturn(secondSave);

        var response = service.createUrl(request);

        assertNotNull(response);
        assertEquals("https://google.com", response.getOriginalUrl());
        assertEquals("http://localhost:8080/W7H", response.getShortUrl());
        assertEquals("W7H", response.getUrlId());
        assertEquals(expirationDate, response.getExpirationDate());

        verify(repository, times(2)).save(any(UrlEntity.class));
    }

    @Test
    void shouldReturnOriginalUrlWhenShortIdExists() {
        UrlEntity entity = UrlEntity.builder()
                .id(1L)
                .originalUrl("https://google.com")
                .shortUrl("W7H")
                .createdAt(LocalDateTime.now())
                .clickCount(0L)
                .build();

        when(idObfuscator.desobfuscate(anyLong())).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(any(UrlEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = service.getOriginalUrlByShortCode("W7H");

        assertEquals("https://google.com", result);
        assertEquals(1L, entity.getClickCount());

        verify(repository).findById(1L);
        verify(repository).save(entity);
    }

    @Test
    void shouldThrowExceptionWhenShortIdDoesNotExist() {
        when(idObfuscator.desobfuscate(anyLong())).thenReturn(999L);
        when(repository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getOriginalUrlByShortCode("abc123"));

        assertEquals("Short URL not found: abc123", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUrlIsExpired() {
        UrlEntity entity = UrlEntity.builder()
                .id(1L)
                .originalUrl("https://google.com")
                .shortUrl("abc123")
                .createdAt(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().minusDays(1))
                .clickCount(0L)
                .build();

        when(idObfuscator.desobfuscate(anyLong())).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getOriginalUrlByShortCode("abc123"));

        assertEquals("Short URL expired: abc123", ex.getMessage());
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}