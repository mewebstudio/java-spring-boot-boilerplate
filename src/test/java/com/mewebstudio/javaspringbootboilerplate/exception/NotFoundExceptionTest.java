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
@DisplayName("Unit test for NotFoundException")
class NotFoundExceptionTest {
    @Test
    @DisplayName("Test NotFoundException")
    void testHandleNotFoundException() {
        // Given
        NotFoundException exception = Instancio.create(NotFoundException.class);
        AppExceptionHandler exceptionHandler = Instancio.create(AppExceptionHandler.class);
        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFoundException(exception);
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
