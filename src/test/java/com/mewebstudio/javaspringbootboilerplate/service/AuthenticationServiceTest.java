package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.security.JwtUserDetails;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for MessageSourceService")
class AuthenticationServiceTest {
    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    SecurityContext securityContext;

    @Mock
    Authentication authentication;

    private final User user = Instancio.create(User.class);
    private final JwtUserDetails jwtUserDetails = JwtUserDetails.create(user);

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(jwtUserDetails);
    }

    @Nested
    @DisplayName("Test class for isAuthorized scenarios")
    class IsAuthorized {
        final String[] roles = {"ADMIN", "USER"};

        @Test
        @DisplayName("Should return true when user has required role")
        void given_whenIsAuthorized_shouldReturnTrueWhenUserHasRequiredRole() {
            // When
            boolean result = authenticationService.isAuthorized(roles);
            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when user does not have required role")
        void given_whenIsAuthorized_shouldReturnFalseWhenUserDoesNotHaveRequiredRole() {
            // When
            boolean result = authenticationService.isAuthorized("XXX");
            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return access denied when user is null")
        void given_whenIsAuthorized_thenShouldReturnAccessDeniedExceptionWhenUserIdNull() {
            // Given
            when(authenticationService.getPrincipal()).thenReturn(null);
            // When
            Executable executable = () -> authenticationService.isAuthorized(roles);
            // Then
            assertThrows(AccessDeniedException.class, executable);
            assertEquals(messageSourceService.get("access_denied"),
                assertThrows(AccessDeniedException.class, executable).getMessage());
        }

        @Test
        @DisplayName("Should return access denied when role is null")
        void given_whenIsAuthorized_thenShouldReturnAccessDeniedExceptionWhenRoleIsNull() {
            // When
            Executable executable = () -> authenticationService.isAuthorized((String[]) null);
            // Then
            assertThrows(AccessDeniedException.class, executable);
            assertEquals(messageSourceService.get("access_denied"),
                assertThrows(AccessDeniedException.class, executable).getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for getPrinciple scenarios")
    class GetPrincipleTest {
        @Test
        @DisplayName("Should return JwtUserDetails when user is authenticated")
        void given_whenGetPrincipal_thenAssertBody() {
            // When
            JwtUserDetails result = authenticationService.getPrincipal();
            // Then
            assertEquals(jwtUserDetails, result);
        }

        @Test
        @DisplayName("Should return null when user is not authenticated")
        void given_whenGetPrincipal_thenShouldHandleClassCastException() {
            // Given
            when(authenticationService.getPrincipal()).thenThrow(new ClassCastException());
            // When
            JwtUserDetails result = authenticationService.getPrincipal();
            // Then
            assertNull(result);
        }
    }
}
