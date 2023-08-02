package com.mewebstudio.javaspringbootboilerplate.controller;

import com.mewebstudio.javaspringbootboilerplate.dto.request.auth.LoginRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.auth.RegisterRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.response.SuccessResponse;
import com.mewebstudio.javaspringbootboilerplate.dto.response.auth.TokenResponse;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.service.AuthService;
import com.mewebstudio.javaspringbootboilerplate.service.MessageSourceService;
import com.mewebstudio.javaspringbootboilerplate.service.UserService;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for AuthController")
class AuthControllerTest {
    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private MessageSourceService messageSourceService;

    private final LoginRequest loginRequest = Instancio.create(LoginRequest.class);

    private final TokenResponse tokenResponse = Instancio.create(TokenResponse.class);

    private final User user = Instancio.create(User.class);

    @Test
    @DisplayName("Test for login")
    void given_whenLogin_thenAssertBody() {
        // Given
        when(authService.login(loginRequest.getEmail(), loginRequest.getPassword(), loginRequest.getRememberMe()))
            .thenReturn(tokenResponse);
        // When
        ResponseEntity<TokenResponse> response = authController.login(loginRequest);
        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(tokenResponse, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Test for register")
    void given_whenRegister_thenAssertBody() throws BindException {
        // Given
        String message = "registered";
        RegisterRequest loginRequest = Instancio.create(RegisterRequest.class);
        when(userService.register(loginRequest)).thenReturn(user);
        when(messageSourceService.get(message)).thenReturn(message);
        // When
        ResponseEntity<SuccessResponse> response = authController.register(loginRequest);
        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(message, response.getBody().getMessage());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Test for email verification")
    void given_whenEmailVerification_thenAssertBody() {
        // Given
        String token = "token";
        String message = "your_email_verified";
        doNothing().when(userService).verifyEmail(token);
        when(messageSourceService.get(message)).thenReturn(message);
        // When
        ResponseEntity<SuccessResponse> response = authController.emailVerification(token);
        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(message, response.getBody().getMessage());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Test for refresh")
    void given_whenRefresh_thenAssertBody() {
        // Given
        String refreshToken = "refreshToken";
        when(authService.refreshFromBearerString("refreshToken")).thenReturn(tokenResponse);
        // When
        ResponseEntity<TokenResponse> response = authController.refresh(refreshToken);
        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(tokenResponse, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
