package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.dto.response.auth.TokenResponse;
import com.mewebstudio.javaspringbootboilerplate.entity.JwtToken;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.exception.NotFoundException;
import com.mewebstudio.javaspringbootboilerplate.exception.RefreshTokenExpiredException;
import com.mewebstudio.javaspringbootboilerplate.security.JwtTokenProvider;
import com.mewebstudio.javaspringbootboilerplate.security.JwtUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static com.mewebstudio.javaspringbootboilerplate.util.Constants.TOKEN_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for AuthService")
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    HttpServletRequest httpServletRequest;

    private final User user = Instancio.create(User.class);

    private final TokenResponse tokenResponse = Instancio.create(TokenResponse.class);

    private final String bearerToken = "Bearer token";

    private final String token = "token";

    @BeforeEach
    void setUp() {
        lenient().when(jwtTokenProvider.getTokenExpiresIn()).thenReturn(1L);
        lenient().when(jwtTokenProvider.generateJwt(anyString())).thenReturn(tokenResponse.getToken());
        lenient().when(jwtTokenProvider.generateRefresh(anyString())).thenReturn(tokenResponse.getRefreshToken());
    }

    @Nested
    @DisplayName("Test class for login scenarios")
    class LoginTest {
        @Mock
        Authentication authentication;

        private final String password = "pass";

        private final JwtUserDetails jwtUserDetails = JwtUserDetails.create(user);

        @BeforeEach
        void setUp() {
            lenient().when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
            lenient().when(jwtTokenProvider.getPrincipal(any(Authentication.class))).thenReturn(jwtUserDetails);
        }

        @Test
        @DisplayName("Test for successful login with username")
        void given_whenLoginWithUsername_thenAssertBody() {
            // When
            when(userService.findByEmail(anyString())).thenReturn(user);
            TokenResponse response = authService.login(user.getEmail(), password, true);
            // Then
            assertNotNull(response);
            assertEquals(tokenResponse.getToken(), response.getToken());
            assertEquals(tokenResponse.getRefreshToken(), response.getRefreshToken());
        }

        @Test
        @DisplayName("Test for successful login with username")
        void given_whenLoginWithEmail_thenAssertBody() {
            // Given
            when(userService.findByEmail(anyString())).thenReturn(user);
            // When
            TokenResponse response = authService.login(user.getEmail(), password, true);
            // Then
            assertNotNull(response);
            assertEquals(tokenResponse.getToken(), response.getToken());
            assertEquals(tokenResponse.getRefreshToken(), response.getRefreshToken());
        }

        @Test
        @DisplayName("Test for not found error login with username")
        void given_whenLoginWithEmail_thenShouldThrowAuthenticationCredentialsNotFoundException_fromNotFound() {
            // Given
            String message = messageSourceService.get("bad_credentials");
            when(userService.findByEmail(anyString()))
                .thenThrow(new NotFoundException(message));
            // When
            Executable executable = () -> authService.login(user.getEmail(), password, true);
            // Then
            assertThrows(AuthenticationCredentialsNotFoundException.class, executable);
        }

        @Test
        @DisplayName("Test for bad credentials error login")
        void given_whenLogin_thenShouldAuthenticationCredentialsNotFoundException() {
            // Given
            when(userService.findByEmail(anyString())).thenReturn(user);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new NotFoundException());
            // When
            Executable executable = () -> authService.login(user.getEmail(), password, true);
            // Then
            assertThrows(AuthenticationCredentialsNotFoundException.class, executable);
        }
    }

    @Nested
    @DisplayName("Test class for refresh scenarios")
    class RefreshTest {
        private final String token = "token";

        @Test
        @DisplayName("Test for successful refresh")
        void given_whenRefresh_thenAssertBody() {
            // Given
            when(jwtTokenProvider.extractJwtFromBearerString(any(String.class))).thenReturn(token);
            when(jwtTokenProvider.validateToken(token)).thenReturn(true);
            when(jwtTokenProvider.getUserFromToken(token)).thenReturn(user);
            when(jwtTokenProvider.generateJwt(user.getId().toString())).thenReturn("newToken");
            when(jwtTokenProvider.generateRefresh(user.getId().toString())).thenReturn("newRefresh");
            // When
            TokenResponse response = authService.refreshFromBearerString(token);
            // Then
            assertNotNull(response);
            assertEquals("newToken", response.getToken());
            assertEquals("newRefresh", response.getRefreshToken());
        }

        @Test
        @DisplayName("Test for expired token")
        void given_whenRefresh_thenAssertBody_FromRefreshTokenExpiredException() {
            // Given
            when(jwtTokenProvider.extractJwtFromBearerString(any(String.class))).thenReturn(token);
            when(jwtTokenProvider.validateToken(token)).thenReturn(false);
            // When
            Executable executable = () -> authService.refreshFromBearerString(token);
            // Then
            assertThrows(RefreshTokenExpiredException.class, executable);
        }
    }

    @Nested
    @DisplayName("Test class for refreshFromBearerString scenarios")
    class RefreshFromBearerStringTest {
        @Test
        @DisplayName("Test for successful refreshBearerString")
        void given_whenRefreshFromBearerString_thenAssertBody() {
            // Given
            JwtToken oldToken = Instancio.create(JwtToken.class);
            oldToken.setRememberMe(true);
            when(jwtTokenProvider.extractJwtFromBearerString(bearerToken)).thenReturn(token);
            when(jwtTokenProvider.validateToken("token")).thenReturn(true);
            when(jwtTokenService.findByUserIdAndRefreshToken(user.getId(), token)).thenReturn(oldToken);
            when(jwtTokenProvider.getUserFromToken("token")).thenReturn(user);
            when(jwtTokenProvider.generateJwt(user.getId().toString())).thenReturn("newToken");
            when(jwtTokenProvider.generateRefresh(user.getId().toString())).thenReturn("newRefresh");
            when(jwtTokenProvider.getTokenExpiresIn()).thenReturn(1L);
            // When
            TokenResponse response = authService.refreshFromBearerString(bearerToken);
            // Then
            assertNotNull(response);
            assertEquals("newToken", response.getToken());
            assertEquals("newRefresh", response.getRefreshToken());
        }
    }

    @Nested
    @DisplayName("Test class for resetPassword scenarios")
    class ResetPasswordTest {
        @Test
        @DisplayName("Test for successful resetPassword")
        void given_whenResetPassword_thenAssertBody() {
            // Given
            doNothing().when(userService).sendEmailPasswordResetMail(user.getEmail());
            // When
            authService.resetPassword(user.getEmail());
            // Then
            verify(userService, times(1)).sendEmailPasswordResetMail(user.getEmail());
        }

        @Test
        @DisplayName("Test for not found error resetPassword")
        void given_whenResetPassword_thenShouldThrowNotFoundException() {
            // Given
            String message = messageSourceService.get("not_found_with_param", new String[]{"email"});
            doThrow(new NotFoundException(message)).when(userService).sendEmailPasswordResetMail(user.getEmail());
            // When
            Executable executable = () -> authService.resetPassword(user.getEmail());
            // Then
            assertThrows(NotFoundException.class, executable);
        }
    }

    @Nested
    @DisplayName("Test class for logout scenarios")
    class LogoutTest {
        private final User user = Instancio.create(User.class);

        private final JwtToken jwtToken = Instancio.create(JwtToken.class);

        @BeforeEach
        void setUp() {
            jwtToken.setUserId(user.getId());
        }

        @Test
        @DisplayName("Test for successful logout")
        void given_whenLogoutWithBearerToken_thenAssertBody() {
            // Given
            when(jwtTokenProvider.extractJwtFromBearerString(bearerToken)).thenReturn(token);
            when(jwtTokenService.findByTokenOrRefreshToken(token)).thenReturn(jwtToken);
            // When
            authService.logout(user, bearerToken);
            // Then
            verify(jwtTokenProvider, times(1)).extractJwtFromBearerString(bearerToken);
            verify(jwtTokenService, times(1)).findByTokenOrRefreshToken(token);
            verify(jwtTokenService, times(1)).delete(any());
        }

        @Test
        @DisplayName("Test for successful logout")
        void given_whenLogoutWithoutBearerToken_thenAssertBody() {
            // Given
            when(httpServletRequest.getHeader(TOKEN_HEADER)).thenReturn(bearerToken);
            when(jwtTokenProvider.extractJwtFromBearerString(bearerToken)).thenReturn(token);
            when(jwtTokenService.findByTokenOrRefreshToken(token)).thenReturn(jwtToken);
            // When
            authService.logout(user);
            // Then
            verify(jwtTokenProvider, times(1)).extractJwtFromBearerString(bearerToken);
            verify(jwtTokenService, times(1)).findByTokenOrRefreshToken(token);
            verify(jwtTokenService, times(1)).delete(any());
        }

        @Test
        @DisplayName("Test for authentication credentials not found exception")
        void given_whenLogoutWithBearerToken_thenThrowAuthenticationCredentialsNotFoundException() {
            // Given
            jwtToken.setUserId(UUID.randomUUID());
            when(jwtTokenProvider.extractJwtFromBearerString(bearerToken)).thenReturn(token);
            when(jwtTokenService.findByTokenOrRefreshToken(token)).thenReturn(jwtToken);
            // When
            Executable executable = () -> authService.logout(user, bearerToken);
            // Then
            assertThrows(AuthenticationCredentialsNotFoundException.class, executable);
        }
    }
}
