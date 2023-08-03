package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.entity.JwtToken;
import com.mewebstudio.javaspringbootboilerplate.exception.NotFoundException;
import com.mewebstudio.javaspringbootboilerplate.repository.JwtTokenRepository;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Tag("unit")
@DisplayName("Unit tests for JwtTokenService")
class JwtTokenServiceTest {
    @Mock
    private JwtTokenRepository jwtTokenRepository;

    @Mock
    private MessageSourceService messageSourceService;

    @InjectMocks
    private JwtTokenService jwtTokenService;

    private final JwtToken jwtToken = Instancio.create(JwtToken.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Test class for findByUserIdAndRefreshToken scenarios")
    class FindByUserIdAndRefreshTokenTest {
        @Test
        @DisplayName("Happy path")
        void given_whenFindByUserIdAndRefreshToken_thenAssertBody() {
            // Given
            UUID userId = UUID.randomUUID();
            String refreshToken = "testRefreshToken";
            when(jwtTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken))
                .thenReturn(Optional.of(jwtToken));
            // When
            JwtToken result = jwtTokenService.findByUserIdAndRefreshToken(userId, refreshToken);
            // Then
            assertEquals(jwtToken, result);
            verify(jwtTokenRepository, times(1))
                .findByUserIdAndRefreshToken(userId, refreshToken);
        }

        @Test
        @DisplayName("Not found exception")
        void given_whenFindByUserIdAndRefreshToken_thenThrowNotFoundException() {
            // Given
            UUID userId = UUID.randomUUID();
            String refreshToken = "testRefreshToken";
            when(jwtTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken)).thenReturn(Optional.empty());
            when(messageSourceService.get("not_found_with_param",
                new String[]{messageSourceService.get("token")})).thenReturn("Token not found");
            // When
            Executable executable = () -> jwtTokenService.findByUserIdAndRefreshToken(userId, refreshToken);
            // Then
            assertThrows(NotFoundException.class, executable);
            verify(jwtTokenRepository, times(1)).findByUserIdAndRefreshToken(userId, refreshToken);
        }
    }

    @Nested
    @DisplayName("Test class for findByTokenOrRefreshToken scenarios")
    class FindByTokenOrRefreshTokenTest {
        private final String token = "testToken";

        @Test
        @DisplayName("Happy path")
        void given_whenFindByTokenOrRefreshToken_thenAssertBody() {
            // Given
            when(jwtTokenRepository.findByTokenOrRefreshToken(token, token)).thenReturn(Optional.of(jwtToken));
            // When
            JwtToken result = jwtTokenService.findByTokenOrRefreshToken(token);
            // Then
            assertEquals(jwtToken, result);
            verify(jwtTokenRepository, times(1)).findByTokenOrRefreshToken(token, token);
        }

        @Test
        @DisplayName("Not found exception")
        void given_whenFindByTokenOrRefreshToken_thenThrowNotFoundException() {
            // Given
            when(jwtTokenRepository.findByTokenOrRefreshToken(token, token)).thenReturn(Optional.empty());
            when(messageSourceService.get("not_found_with_param", new String[]{messageSourceService.get("token")})).thenReturn("Token not found");
            // When
            Executable executable = () -> jwtTokenService.findByTokenOrRefreshToken(token);
            // Then
            assertThrows(NotFoundException.class, executable);
            verify(jwtTokenRepository, times(1)).findByTokenOrRefreshToken(token, token);
        }
    }

    @Test
    @DisplayName("Test class for save scenarios")
    void testSave() {
        // When
        jwtTokenService.save(jwtToken);
        // Then
        verify(jwtTokenRepository, times(1)).save(jwtToken);
    }

    @Test
    @DisplayName("Test class for delete scenarios")
    void testDelete() {
        // When
        jwtTokenService.delete(jwtToken);
        // Then
        verify(jwtTokenRepository, times(1)).delete(jwtToken);
    }
}
