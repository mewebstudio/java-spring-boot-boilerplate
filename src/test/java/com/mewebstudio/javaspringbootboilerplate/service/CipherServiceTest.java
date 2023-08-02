package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.Constants;
import com.mewebstudio.javaspringbootboilerplate.exception.CipherException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@DisplayName("Unit tests for CipherService")
class CipherServiceTest {
    @InjectMocks
    private CipherService cipherService;

    @Mock
    private AESCipherService aesCipherService;

    private final String plainText = "Hello, World!";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Test class for encrypt scenarios")
    class EncryptTest {
        @Test
        @DisplayName("Happy path with secret key")
        public void given_whenEncryptWithSecretKey_thenAssertBody() throws Exception {
            // Given
            String encryptedText = "encryptedText";
            when(aesCipherService.encrypt(plainText, Constants.APP_SECRET_KEY)).thenReturn(encryptedText);
            // When
            String result = cipherService.encrypt(plainText, Constants.APP_SECRET_KEY);
            // Then
            assertEquals(encryptedText, result);
            verify(aesCipherService, times(1)).encrypt(plainText, Constants.APP_SECRET_KEY);
        }

        @Test
        @DisplayName("Encrypting error test with secret key")
        public void given_whenEncryptWithSecretKey_thenThrowCipherException() throws Exception {
            // Given
            when(aesCipherService.encrypt(plainText, Constants.APP_SECRET_KEY)).thenThrow(new CipherException());
            // When
            Executable executable = () -> cipherService.encrypt(plainText, Constants.APP_SECRET_KEY);
            // Then
            assertThrows(CipherException.class, executable);
        }
    }
}
