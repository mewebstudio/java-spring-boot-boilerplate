package com.mewebstudio.javaspringbootboilerplate.exception;

import com.mewebstudio.javaspringbootboilerplate.dto.response.ErrorResponse;
import com.mewebstudio.javaspringbootboilerplate.service.MessageSourceService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.multipart.MultipartException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Tag("unit")
@DisplayName("Unit tests for AppExceptionHandler")
class AppExceptionHandlerTest {
    @InjectMocks
    private AppExceptionHandler appExceptionHandler;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private HttpRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test handleHttpRequestMethodNotSupported")
    void testHandleHttpRequestMethodNotSupported() {
        // Given
        HttpRequestMethodNotSupportedException exception = Instancio.create(
            HttpRequestMethodNotSupportedException.class);
        // When
        ResponseEntity<ErrorResponse> response = appExceptionHandler.handleHttpRequestMethodNotSupported(exception);
        // Then
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        verify(messageSourceService).get("method_not_supported");
    }

    @Test
    @DisplayName("Test handleHttpMessageNotReadable")
    void testHandleHttpMessageNotReadable() {
        // Given
        HttpMessageNotReadableException exception = Instancio.create(HttpMessageNotReadableException.class);
        // When
        ResponseEntity<ErrorResponse> response = appExceptionHandler.handleHttpMessageNotReadable(exception);
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(messageSourceService).get("malformed_json_request");
    }

    @Test
    @DisplayName("Test handleBindException")
    void testHandleBindException() {
        // Given
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
        bindingResult.addError(new FieldError(bindingResult.getObjectName(), "field", "message"));
        BindException exception = new BindException(bindingResult);
        // When
        ResponseEntity<ErrorResponse> response = appExceptionHandler.handleBindException(exception);
        // Then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        verify(messageSourceService).get("validation_error");
    }

    @Test
    @DisplayName("Test handleBadRequestException for BadRequestException")
    void testHandleBadRequestExceptionForBadRequestException() {
        // Given
        String errorMessage = "Bad request error message";
        BadRequestException exception = new BadRequestException(errorMessage);

        // When
        ResponseEntity<ErrorResponse> response = appExceptionHandler.handleBadRequestException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    @DisplayName("Test handleBadRequestException for MultipartException")
    void testHandleBadRequestExceptionForMultipartException() {
        // Given
        MultipartException exception = Instancio.create(MultipartException.class);
        when(messageSourceService.get("multipart_exception")).thenReturn(exception.getMessage());
        // When
        ResponseEntity<ErrorResponse> response = appExceptionHandler.handleBadRequestException(exception);
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(exception.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    @DisplayName("Test handleTokenExpiredRequestException for Exception")
    void testHandleTokenExpiredRequestException() {
        // Given
        TokenExpiredException exception = Instancio.create(TokenExpiredException.class);
        when(messageSourceService.get("token_expired")).thenReturn(exception.getMessage());
        // When
        ResponseEntity<ErrorResponse> response =
            appExceptionHandler.handleTokenExpiredRequestException(exception);
        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(exception.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    @DisplayName("Test handleNotFoundException for Exception")
    void testHandleNotFoundException() {
        // Given
        NotFoundException exception = Instancio.create(NotFoundException.class);
        when(messageSourceService.get("not_found")).thenReturn(exception.getMessage());
        // When
        ResponseEntity<ErrorResponse> response = appExceptionHandler.handleNotFoundException(exception);
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(exception.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    @DisplayName("Test handleBadCredentialsException for Exception")
    void testHandleBadCredentialsException() {
        // Given
        BadCredentialsException exception = Instancio.create(BadCredentialsException.class);
        when(messageSourceService.get("unauthorized")).thenReturn(exception.getMessage());
        // When
        ResponseEntity<ErrorResponse> response = appExceptionHandler.handleBadCredentialsException(exception);
        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(exception.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    @DisplayName("Test handleAccessDeniedException for Exception")
    void testHandleAccessDeniedException() {
        // Given
        AccessDeniedException exception = Instancio.create(AccessDeniedException.class);
        when(messageSourceService.get("unauthorized")).thenReturn(exception.getMessage());
        // When
        ResponseEntity<ErrorResponse> response = appExceptionHandler.handleAccessDeniedException(exception);
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(exception.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    @DisplayName("Test handleExpectationException for Exception")
    void testHandleExpectationException() {
        // Given
        ExpectationException exception = Instancio.create(ExpectationException.class);
        when(messageSourceService.get("expectation_error")).thenReturn(exception.getMessage());
        // When
        ResponseEntity<ErrorResponse> response = appExceptionHandler.handleExpectationException(exception);
        // Then
        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        assertEquals(exception.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    @DisplayName("Test handleAllExceptions for Exception")
    void testHandleAllExceptions() {
        // Given
        Exception exception = Instancio.create(Exception.class);
        when(messageSourceService.get("server_error")).thenReturn(exception.getMessage());
        // When
        ResponseEntity<ErrorResponse> response = appExceptionHandler.handleAllExceptions(exception);
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(exception.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
    }
}
