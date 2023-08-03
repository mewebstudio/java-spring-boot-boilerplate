package com.mewebstudio.javaspringbootboilerplate.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for MessageSourceService")
class MessageSourceServiceTest {
    @InjectMocks
    private MessageSourceService messageSourceService;

    @Mock
    private MessageSource messageSource;

    @Nested
    @DisplayName("Test class for get scenarios")
    class GetTest {
        @Test
        @DisplayName("Test get with code, params, locale")
        void given_whenGetWithCodeParamsLocale_thenAssertBody() {
            // Given
            String code = "code";
            String message = "message";
            Locale locale = LocaleContextHolder.getLocale();
            when(messageSource.getMessage(code, new Object[0], locale))
                .thenReturn(message);
            when(messageSourceService.get(code, new Object[0], locale))
                .thenReturn(message);
            // When
            String result = messageSourceService.get(code, new Object[0], locale);
            // Then
            assertEquals(message, result);
        }

        @Test
        @DisplayName("Test get with code, params, locale")
        void given_whenGetWithCodeParamsLocale_thenNoSuchMessageAssertBody() {
            // Given
            String code = "code";
            Locale locale = LocaleContextHolder.getLocale();
            when(messageSource.getMessage(code, new Object[0], locale))
                .thenReturn(code);
            when(messageSourceService.get(code, new Object[0], locale))
                .thenReturn(code);
            // When
            String result = messageSourceService.get(code, new Object[0], locale);
            // Then
            assertEquals(code, result);
        }

        @Test
        @DisplayName("Test get with code, params")
        void given_whenGetWithCodeParams_thenAssertBody() {
            // Given
            String code = "code";
            String message = "message";
            when(messageSourceService.get(code, new Object[0])).thenReturn(message);
            // When
            String result = messageSourceService.get(code, new Object[0]);
            // Then
            assertEquals(message, result);
        }

        @Test
        @DisplayName("Test get with code, locale")
        void given_whenGetWithCodeLocale_thenAssertBody() {
            // Given
            String code = "code";
            String message = "message";
            Locale locale = LocaleContextHolder.getLocale();
            when(messageSourceService.get(code, locale)).thenReturn(message);
            // When
            String result = messageSourceService.get(code, locale);
            // Then
            assertEquals(message, result);
        }

        @Test
        @DisplayName("Test get with code")
        void given_whenGetWithCode_thenAssertBody() {
            // Given
            String code = "code";
            String message = "message";
            when(messageSourceService.get(code)).thenReturn(message);
            // When
            String result = messageSourceService.get(code, new Object[0]);
            // Then
            assertEquals(message, result);
        }

        @Test
        @DisplayName("Test no such message")
        void given_whenGetWithCode_thenNoSuchMessageAssertBody() {
            // Given
            String code = "code";
            when(messageSourceService.get(code)).thenThrow(new NoSuchMessageException(code));
            // When
            String result = messageSourceService.get(code, new Object[0]);
            // Then
            assertEquals(code, result);
        }
    }
}
