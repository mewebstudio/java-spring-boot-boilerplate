package com.mewebstudio.javaspringbootboilerplate.exception;

import com.mewebstudio.javaspringbootboilerplate.dto.response.ErrorResponse;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
@DisplayName("Unit test for ExpectationException")
class RefreshTokenExpiredExceptionTest {
    @Test
    @DisplayName("Test ExpectationException")
    void testHandleExpectationException() {
        // Given
        RefreshTokenExpiredException exception = Instancio.create(RefreshTokenExpiredException.class);
        AppExceptionHandler exceptionHandler = Instancio.create(AppExceptionHandler.class);
        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTokenExpiredRequestException(exception);
        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Test ExpectationException with message")
    void testHandleExpectationExceptionWithMessage() {
        // Given
        RefreshTokenExpiredException exception = new RefreshTokenExpiredException("Expectation exception!");
        AppExceptionHandler exceptionHandler = Instancio.create(AppExceptionHandler.class);
        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTokenExpiredRequestException(exception);
        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
