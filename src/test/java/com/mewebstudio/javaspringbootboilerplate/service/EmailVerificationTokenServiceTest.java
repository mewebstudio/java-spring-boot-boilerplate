package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.entity.EmailVerificationToken;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.exception.BadRequestException;
import com.mewebstudio.javaspringbootboilerplate.exception.NotFoundException;
import com.mewebstudio.javaspringbootboilerplate.repository.EmailVerificationTokenRepository;
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
@DisplayName("Unit tests for EmailVerificationTokenService")
class EmailVerificationTokenServiceTest {
    @InjectMocks
    private EmailVerificationTokenService emailVerificationTokenService;

    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Mock
    private MessageSourceService messageSourceService;

    private final User user = Instancio.create(User.class);

    private final EmailVerificationToken token = Instancio.create(EmailVerificationToken.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user.setEmailVerificationToken(token);
        token.setExpirationDate(Date.from(Instant.now().plusSeconds(3600)));
        token.setUser(user);

        lenient().when(emailVerificationTokenRepository.findByToken(anyString())).thenReturn(Optional.of(token));
        lenient().when(emailVerificationTokenRepository.findByUserId(any(UUID.class))).thenReturn(Optional.of(token));
        lenient().when(emailVerificationTokenRepository.save(any(EmailVerificationToken.class))).thenReturn(token);
        lenient().when(messageSourceService.get(anyString())).thenReturn("Error Message");

        // Set expiresIn value for the service instance
        emailVerificationTokenService = new EmailVerificationTokenService(
            emailVerificationTokenRepository,
            messageSourceService,
            3600L // Set the appropriate expiresIn value here
        );
    }

    @Nested
    @DisplayName("Test class for isEmailVerificationTokenExpired scenarios")
    class IsEmailVerificationTokenExpiredTest {
        @Test
        @DisplayName("Happy path")
        void given_whenIsEmailVerificationTokenExpired_thenAssertBody() {
            // When
            boolean isExpired = emailVerificationTokenService.isEmailVerificationTokenExpired(token);
            // Then
            assertFalse(isExpired);
        }

        @Test
        @DisplayName("Test isEmailVerificationTokenExpired with expired token")
        void givenExpiredToken_whenIsEmailVerificationTokenExpired_thenTrue() {
            // When
            token.setExpirationDate(Date.from(Instant.now().minusSeconds(3600))); // 1 hour ago
            // Then
            boolean emailVerificationTokenExpired = emailVerificationTokenService.isEmailVerificationTokenExpired(token);
            assertTrue(emailVerificationTokenExpired);
        }
    }

    @Nested
    @DisplayName("Test class for create scenarios")
    class CreateTest {
        @Test
        @DisplayName("Test create email verification token")
        void givenUser_whenCreate_thenTokenCreated() {
            // Given
            when(emailVerificationTokenRepository.save(any(EmailVerificationToken.class))).thenReturn(token);
            // When
            EmailVerificationToken createdToken = emailVerificationTokenService.create(user);
            // Then
            assertNotNull(createdToken);
            assertEquals(user, createdToken.getUser());
            assertEquals(token.getToken(), createdToken.getToken());
            assertEquals(token.getExpirationDate(), createdToken.getExpirationDate());

            verify(emailVerificationTokenRepository, times(1)).save(any(EmailVerificationToken.class));
        }

        @Test
        @DisplayName("Test create email verification token")
        void givenUser_whenCreate_thenOldTokenNotPresentAndTokenCreated() {
            // Given
            when(emailVerificationTokenRepository.findByUserId(any(UUID.class))).thenReturn(Optional.empty());
            when(emailVerificationTokenRepository.save(any(EmailVerificationToken.class))).thenReturn(token);
            // When
            EmailVerificationToken createdToken = emailVerificationTokenService.create(user);
            // Then
            assertNotNull(createdToken);
            assertEquals(user, createdToken.getUser());
            assertEquals(token.getToken(), createdToken.getToken());
            assertEquals(token.getExpirationDate(), createdToken.getExpirationDate());

            verify(emailVerificationTokenRepository, times(1)).save(any(EmailVerificationToken.class));
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
            when(emailVerificationTokenRepository.findByToken(anyString())).thenReturn(Optional.of(token));
            // When
            User retrievedUser = emailVerificationTokenService.getUserByToken(tokenValue);
            // Then
            assertNotNull(retrievedUser);
            assertEquals(user, retrievedUser);
            verify(emailVerificationTokenRepository, times(1)).findByToken(anyString());
        }

        @Test
        @DisplayName("Test getUserByToken with expired token")
        void givenExpiredToken_whenGetUserByToken_thenBadRequestExceptionThrown() {
            // Given
            token.setExpirationDate(Date.from(Instant.now().minusSeconds(3600))); // 1 hour ago
            when(emailVerificationTokenRepository.findByToken(anyString())).thenReturn(Optional.of(token));
            // When
            Executable executable = () -> emailVerificationTokenService.getUserByToken(tokenValue);
            // Then
            assertThrows(BadRequestException.class, executable);
            verify(emailVerificationTokenRepository, times(1)).findByToken(anyString());
            verify(messageSourceService, times(1)).get("verification_token_expired");
        }

        @Test
        @DisplayName("Test getUserByToken with not found token")
        void givenNotFoundToken_whenGetUserByToken_thenNotFoundExceptionThrown() {
            // Given
            when(emailVerificationTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());
            // When
            Executable executable = () -> emailVerificationTokenService.getUserByToken("nonExistentToken");
            // Then
            assertThrows(NotFoundException.class, executable);
            verify(emailVerificationTokenRepository, times(1)).findByToken(anyString());
            verify(messageSourceService, times(1)).get("verification_token_not_found");
        }
    }

    @Test
    @DisplayName("Test deleteByUserId")
    void givenUserId_whenDeleteByUserId_thenTokenDeleted() {
        // Given
        doNothing().when(emailVerificationTokenRepository).deleteByUserId(any(UUID.class));
        // When
        emailVerificationTokenService.deleteByUserId(user.getId());
        // Then
        verify(emailVerificationTokenRepository, times(1)).deleteByUserId(user.getId());
    }
}
