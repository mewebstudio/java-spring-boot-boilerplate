package com.mewebstudio.javaspringbootboilerplate.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mewebstudio.javaspringbootboilerplate.controller.AuthController;
import com.mewebstudio.javaspringbootboilerplate.dto.request.auth.LoginRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.auth.PasswordRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.auth.ResetPasswordRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.response.auth.TokenResponse;
import com.mewebstudio.javaspringbootboilerplate.entity.PasswordResetToken;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.exception.AppExceptionHandler;
import com.mewebstudio.javaspringbootboilerplate.exception.NotFoundException;
import com.mewebstudio.javaspringbootboilerplate.exception.RefreshTokenExpiredException;
import com.mewebstudio.javaspringbootboilerplate.service.AuthService;
import com.mewebstudio.javaspringbootboilerplate.service.MessageSourceService;
import com.mewebstudio.javaspringbootboilerplate.service.PasswordResetTokenService;
import com.mewebstudio.javaspringbootboilerplate.service.UserService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.mewebstudio.javaspringbootboilerplate.util.Constants.TOKEN_HEADER;
import static com.mewebstudio.javaspringbootboilerplate.util.Constants.TOKEN_TYPE;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("mvcIT")
@ActiveProfiles(value = "mvcIT")
@WebMvcTest(AuthController.class)
@DisplayName("MVC Integration Tests for AccountController")
class AuthControllerMvcIT {
    @Autowired
    private AuthController authController;

    @Autowired
    private AppExceptionHandler appExceptionHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordResetTokenService passwordResetTokenService;

    @MockBean
    private MessageSourceService messageSourceService;

    private MockMvc mockMvc;

    private final TokenResponse tokenResponse = Instancio.create(TokenResponse.class);

    private final PasswordResetToken passwordResetToken = Instancio.create(PasswordResetToken.class);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
            .setControllerAdvice(appExceptionHandler)
            .build();
    }

    @Nested
    @DisplayName("Test class for login scenarios")
    class LoginTest {
        private final LoginRequest request = Instancio.create(LoginRequest.class);

        @BeforeEach
        void setUp() {
            request.setEmail("mail@example.com");
            request.setPassword("P@ssw0rd1.");
        }

        @Test
        @DisplayName("Should return 200 OK with token response")
        void given_whenLogin_thenAssertBody() throws Exception {
            // Given
            when(authService.login(request.getEmail(), request.getPassword(), request.getRememberMe()))
                .thenReturn(tokenResponse);
            // When
            RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
            ResultActions perform = mockMvc.perform(requestBuilder);
            // Then
            perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(tokenResponse.getToken()))
                .andExpect(jsonPath("$.refreshToken").value(tokenResponse.getRefreshToken()))
                .andExpect(jsonPath("$.expiresIn.token").value(tokenResponse.getExpiresIn().getToken()))
                .andExpect(jsonPath("$.expiresIn.refreshToken").value(tokenResponse.getExpiresIn()
                    .getRefreshToken()));
        }

        @Test
        @DisplayName("Should return AuthenticationCredentialsNotFoundException")
        void given_whenLogin_thenShouldThrowAuthenticationCredentialsNotFoundException() throws Exception {
            // Given
            when(authService.login(request.getEmail(), request.getPassword(), request.getRememberMe()))
                .thenThrow(Instancio.create(AuthenticationCredentialsNotFoundException.class));
            // When
            RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
            ResultActions perform = mockMvc.perform(requestBuilder);
            // Then
            perform.andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Test class for e-mail verification with token scenarios")
    class EmailVerificationTest {
        private final String token = "token";

        @Test
        @DisplayName("Should return 200 OK with token response")
        void given_whenVerifyEmail_thenAssertBody() throws Exception {
            // Given
            doNothing().when(userService).verifyEmail(token);
            // When
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/auth/email-verification/" + token);
            ResultActions perform = mockMvc.perform(requestBuilder);
            // Then
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 404 Not Found")
        void given_whenVerifyEmail_thenShouldThrowAuthenticationCredentialsNotFoundException() throws Exception {
            // Given
            doThrow(Instancio.create(NotFoundException.class)).when(userService).verifyEmail(token);
            // When
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/auth/email-verification/" + token);
            ResultActions perform = mockMvc.perform(requestBuilder);
            // Then
            perform.andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Test class for refresh token scenarios")
    class RefreshTest {
        @Test
        @DisplayName("Should return 200 OK with token response")
        void given_whenRefresh_thenAssertBody() throws Exception {
            // Given
            when(authService.refreshFromBearerString(String.format("%s %s", TOKEN_TYPE,
                tokenResponse.getRefreshToken()))).thenReturn(tokenResponse);
            // When
            RequestBuilder request = MockMvcRequestBuilders.get("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER, String.format("%s %s", TOKEN_TYPE, tokenResponse.getRefreshToken()));
            ResultActions perform = mockMvc.perform(request);
            // Then
            perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(tokenResponse.getToken()))
                .andExpect(jsonPath("$.refreshToken").value(tokenResponse.getRefreshToken()))
                .andExpect(jsonPath("$.expiresIn.token").value(tokenResponse.getExpiresIn().getToken()))
                .andExpect(jsonPath("$.expiresIn.refreshToken").value(tokenResponse.getExpiresIn()
                    .getRefreshToken()));
        }

        @Test
        @DisplayName("Should throw RefreshTokenExpiredException")
        void given_whenRefresh_thenShouldRefreshTokenExpiredException() throws Exception {
            // Given
            when(authService.refreshFromBearerString(String.format("%s %s", TOKEN_TYPE,
                tokenResponse.getRefreshToken()))).thenThrow(Instancio.create(RefreshTokenExpiredException.class));
            // When
            RequestBuilder request = MockMvcRequestBuilders.get("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER, String.format("%s %s", TOKEN_TYPE, tokenResponse.getRefreshToken()));
            ResultActions perform = mockMvc.perform(request);
            // Then
            perform.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should throw NotFoundException")
        void given_whenRefresh_thenShouldNotFoundException() throws Exception {
            // Given
            when(authService.refreshFromBearerString(String.format("%s %s", TOKEN_TYPE,
                tokenResponse.getRefreshToken()))).thenThrow(Instancio.create(NotFoundException.class));
            // When
            RequestBuilder request = MockMvcRequestBuilders.get("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER, String.format("%s %s", TOKEN_TYPE, tokenResponse.getRefreshToken()));
            ResultActions perform = mockMvc.perform(request);
            // Then
            perform.andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Test class for reset password scenarios")
    class ResetPassword {
        private final PasswordRequest passwordRequest = Instancio.create(PasswordRequest.class);

        @BeforeEach
        void setUp() {
            passwordRequest.setEmail("mail@example.com");
        }

        @Test
        @DisplayName("Should return 200 OK")
        void given_whenResetPassword_thenAssertBody() throws Exception {
            // Given
            String message = "Password reset link sent to your e-mail address";
            doNothing().when(authService).resetPassword(passwordRequest.getEmail());
            when(messageSourceService.get("password_reset_link_sent")).thenReturn(message);
            // When
            RequestBuilder request = MockMvcRequestBuilders.post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordRequest));
            ResultActions perform = mockMvc.perform(request);
            // Then
            perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(message));
        }

        @Test
        @DisplayName("Should throw BindException")
        void given_whenResetPassword_thenShouldThrowBindException() throws Exception {
            // Given
            passwordRequest.setEmail("invalid-email");
            doThrow(Instancio.create(NotFoundException.class)).when(authService)
                .resetPassword(passwordRequest.getEmail());
            // When
            RequestBuilder request = MockMvcRequestBuilders.post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordRequest));
            ResultActions perform = mockMvc.perform(request);
            // Then
            perform.andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("Should throw NotFoundException")
        void given_whenResetPassword_thenShouldThrowNotFoundException() throws Exception {
            // Given
            doThrow(Instancio.create(NotFoundException.class)).when(authService)
                .resetPassword(passwordRequest.getEmail());
            // When
            RequestBuilder request = MockMvcRequestBuilders.post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordRequest));
            ResultActions perform = mockMvc.perform(request);
            // Then
            perform.andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Test class for check token scenarios")
    class ResetPasswordCheckTokenTest {
        @Test
        @DisplayName("Should return 200 OK")
        void given_whenResetPasswordCheckToken_thenAssertBody() throws Exception {
            // Given
            when(passwordResetTokenService.findByToken(passwordResetToken.getToken())).thenReturn(passwordResetToken);
            // When
            RequestBuilder request = MockMvcRequestBuilders
                .get("/auth/reset-password/" + passwordResetToken.getToken());
            ResultActions perform = mockMvc.perform(request);
            // Then
            perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(passwordResetToken.getId().toString()))
                .andExpect(jsonPath("$.token").value(passwordResetToken.getToken()))
                .andExpect(jsonPath("$.expirationDate")
                    .value(passwordResetToken.getExpirationDate()));
        }

        @Test
        @DisplayName("Should throw NotFoundException")
        void given_whenResetPasswordCheckToken_thenShouldThrowNotFoundException() throws Exception {
            // Given
            when(passwordResetTokenService.findByToken(passwordResetToken.getToken()))
                .thenThrow(Instancio.create(NotFoundException.class));
            // When
            RequestBuilder request = MockMvcRequestBuilders
                .get("/auth/reset-password/" + passwordResetToken.getToken());
            ResultActions perform = mockMvc.perform(request);
            // Then
            perform.andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Test class for reset password with token scenarios")
    class ResetPasswordWithTokenTest {
        private final ResetPasswordRequest resetPasswordRequest = Instancio.create(ResetPasswordRequest.class);

        @BeforeEach
        void setUp() {
            resetPasswordRequest.setPassword("newPassword123.");
            resetPasswordRequest.setPasswordConfirm("newPassword123.");
        }

        @Test
        @DisplayName("Should return 200 OK")
        void given_whenResetPasswordWithToken_thenAssertBody() throws Exception {
            // Given
            String message = "Password reset successfully";
            doNothing().when(userService).resetPassword(passwordResetToken.getToken(), resetPasswordRequest);
            when(messageSourceService.get("password_reset_success_successfully")).thenReturn(message);
            // When
            RequestBuilder request = MockMvcRequestBuilders
                .post("/auth/reset-password/" + passwordResetToken.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest));
            ResultActions perform = mockMvc.perform(request);
            // Then
            perform.andExpect(status().isOk()).andExpect(jsonPath("$.message").value(message));
        }

        @Test
        @DisplayName("Should throw BindException")
        void given_whenResetPasswordWithToken_thenShouldThrowBindException() throws Exception {
            // Given
            resetPasswordRequest.setPasswordConfirm("wrongPassword");
            doThrow(Instancio.create(NotFoundException.class)).when(userService)
                .resetPassword(passwordResetToken.getToken(), resetPasswordRequest);
            // When
            RequestBuilder request = MockMvcRequestBuilders
                .post("/auth/reset-password/" + passwordResetToken.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest));
            ResultActions perform = mockMvc.perform(request);
            // Then
            perform.andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("Test class for logout scenarios")
    class LogoutTest {
        private final User user = Instancio.create(User.class);

        @Test
        @DisplayName("Should return 200 OK")
        void given_whenLogout_thenAssertBody() throws Exception {
            // Given
            String message = "Logout successfully";
            when(userService.getUser()).thenReturn(user);
            doNothing().when(authService).logout(user);
            when(messageSourceService.get("logout_successfully")).thenReturn(message);
            // When
            RequestBuilder request = MockMvcRequestBuilders.get("/auth/logout");
            ResultActions perform = mockMvc.perform(request);
            // Then
            perform.andExpect(status().isOk()).andExpect(jsonPath("$.message").value(message));
        }

        @Test
        @DisplayName("Should throw BadCredentialsException")
        void given_whenLogout_thenShouldThrowBadCredentialsException() throws Exception {
            // Given
            when(userService.getUser()).thenReturn(user);
            doThrow(Instancio.create(NotFoundException.class)).when(authService).logout(user);
            // When
            RequestBuilder request = MockMvcRequestBuilders.get("/auth/logout");
            ResultActions perform = mockMvc.perform(request);
            // Then
            perform.andExpect(status().isNotFound());
        }
    }
}
