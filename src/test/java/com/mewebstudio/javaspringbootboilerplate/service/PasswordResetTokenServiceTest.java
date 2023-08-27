package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.entity.PasswordResetToken;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.exception.BadRequestException;
import com.mewebstudio.javaspringbootboilerplate.exception.NotFoundException;
import com.mewebstudio.javaspringbootboilerplate.repository.PasswordResetTokenRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Tag("unit")
@DisplayName("Unit tests for PasswordResetTokenService")
class PasswordResetTokenServiceTest {
    @InjectMocks
    private PasswordResetTokenService passwordResetTokenService;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private MessageSourceService messageSourceService;

    private final User user = Instancio.create(User.class);

    private final PasswordResetToken token = Instancio.create(PasswordResetToken.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user.setPasswordResetToken(token);
        token.setExpirationDate(Date.from(Instant.now().plusSeconds(3600)));
        token.setUser(user);

        lenient().when(passwordResetTokenRepository.findByToken(anyString())).thenReturn(Optional.of(token));
        lenient().when(passwordResetTokenRepository.findByUserId(any(UUID.class))).thenReturn(Optional.of(token));
        lenient().when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(token);
        lenient().when(messageSourceService.get(anyString())).thenReturn("Error Message");

        // Set expiresIn value for the service instance
        passwordResetTokenService = new PasswordResetTokenService(
            passwordResetTokenRepository,
            messageSourceService,
            3600L // Set the appropriate expiresIn value here
        );
    }

    @Nested
    @DisplayName("Test class for isPasswordResetTokenExpired scenarios")
    class IsPasswordResetTokenExpiredTest {
        @Test
        @DisplayName("Happy path")
        void given_whenIsPasswordResetTokenExpired_thenAssertBody() {
            // When
            boolean isExpired = passwordResetTokenService.isPasswordResetTokenExpired(token);
            // Then
            assertFalse(isExpired);
        }

        @Test
        @DisplayName("Test isPasswordResetTokenExpired with expired token")
        void givenExpiredToken_whenIsPasswordResetTokenExpired_thenTrue() {
            // When
            token.setExpirationDate(Date.from(Instant.now().minusSeconds(3600))); // 1 hour ago
            // Then
            boolean passwordResetTokenExpired = passwordResetTokenService.isPasswordResetTokenExpired(token);
            assertTrue(passwordResetTokenExpired);
        }
    }

    @Nested
    @DisplayName("Test class for create scenarios")
    class CreateTest {
        @Test
        @DisplayName("Test create password reset token")
        void givenUser_whenCreate_thenTokenCreated() {
            // Given
            when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(token);
            // When
            PasswordResetToken createdToken = passwordResetTokenService.create(user);
            // Then
            assertNotNull(createdToken);
            assertEquals(user, createdToken.getUser());
            assertEquals(token.getToken(), createdToken.getToken());
            assertEquals(token.getExpirationDate(), createdToken.getExpirationDate());

            verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
        }

        @Test
        @DisplayName("Test create password reset token")
        void givenUser_whenCreate_thenOldTokenNotPresentAndTokenCreated() {
            // Given
            when(passwordResetTokenRepository.findByUserId(any(UUID.class))).thenReturn(Optional.empty());
            when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(token);
            // When
            PasswordResetToken createdToken = passwordResetTokenService.create(user);
            // Then
            assertNotNull(createdToken);
            assertEquals(user, createdToken.getUser());
            assertEquals(token.getToken(), createdToken.getToken());
            assertEquals(token.getExpirationDate(), createdToken.getExpirationDate());

            verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
        }
    }

    @Nested
    @DisplayName("Test class for getUserByToken scenarios")
    class GetUserByTokenTest {
        private final String tokenValue = "testToken";

        @Test
        @DisplayName("Test getUserByToken with valid token")
        void givenValidToken_whenGetUserByToken_thenUserReturned() {
            // Given
            when(passwordResetTokenRepository.findByToken(anyString())).thenReturn(Optional.of(token));
            // When
            User retrievedUser = passwordResetTokenService.getUserByToken(tokenValue);
            // Then
            assertNotNull(retrievedUser);
            assertEquals(user, retrievedUser);
            verify(passwordResetTokenRepository, times(1)).findByToken(anyString());
        }

        @Test
        @DisplayName("Test getUserByToken with expired token")
        void givenExpiredToken_whenGetUserByToken_thenBadRequestExceptionThrown() {
            // Given
            token.setExpirationDate(Date.from(Instant.now().minusSeconds(3600))); // 1 hour ago
            when(passwordResetTokenRepository.findByToken(anyString())).thenReturn(Optional.of(token));
            // When
            Executable executable = () -> passwordResetTokenService.getUserByToken(tokenValue);
            // Then
            assertThrows(BadRequestException.class, executable);
            verify(passwordResetTokenRepository, times(1)).findByToken(anyString());
        }

        @Test
        @DisplayName("Test getUserByToken with not found token")
        void givenNotFoundToken_whenGetUserByToken_thenNotFoundExceptionThrown() {
            // Given
            when(passwordResetTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());
            // When
            Executable executable = () -> passwordResetTokenService.getUserByToken("nonExistentToken");
            // Then
            assertThrows(NotFoundException.class, executable);
            verify(passwordResetTokenRepository, times(1)).findByToken(anyString());
        }
    }

    @Test
    @DisplayName("Test deleteByUserId")
    void givenUserId_whenDeleteByUserId_thenTokenDeleted() {
        // Given
        doNothing().when(passwordResetTokenRepository).deleteByUserId(any(UUID.class));
        // When
        passwordResetTokenService.deleteByUserId(user.getId());
        // Then
        verify(passwordResetTokenRepository, times(1)).deleteByUserId(user.getId());
    }
}
