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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
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
            String bearerToken = "Bearer token";
            String token = "token";
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
}
