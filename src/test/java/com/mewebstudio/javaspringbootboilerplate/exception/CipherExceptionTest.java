package com.mewebstudio.javaspringbootboilerplate.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
@DisplayName("Unit test for CipherException")
public class CipherExceptionTest {
    @Test
    @DisplayName("Constructor with message")
    public void testConstructorWithMessage() {
        // Given
        String errorMessage = "Cipher exception message";
        // When
        CipherException exception = new CipherException(errorMessage);
        // Then
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Constructor with message and caouse")
    public void testConstructorWithMessageAndCause() {
        // Given
        String errorMessage = "Cipher exception message";
        Throwable cause = new RuntimeException("Cause exception");
        // When
        CipherException exception = new CipherException(errorMessage, cause);
        // Then
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Constructor with cause")
    public void testConstructorWithCause() {
        // Given
        Throwable cause = new RuntimeException("Cause exception");
        // When
        CipherException exception = new CipherException(cause);
        // Then
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Constructor with message, cause, enableSuppression, writableStackTrace")
    public void testConstructorWithFullParameters() {
        // Given
        String errorMessage = "Cipher exception message";
        Throwable cause = new RuntimeException("Cause exception");
        boolean enableSuppression = true;
        boolean writableStackTrace = false;
        // When
        CipherException exception = new CipherException(errorMessage, cause, enableSuppression, writableStackTrace);
        // Then
        assertEquals(errorMessage, exception.getMessage());
    }
}
