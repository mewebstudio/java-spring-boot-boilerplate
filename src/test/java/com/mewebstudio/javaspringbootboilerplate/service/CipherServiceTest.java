package com.mewebstudio.javaspringbootboilerplate.service;

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
import static org.mockito.ArgumentMatchers.anyString;
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

    private final String secretKey = "testKey";

    private final String plainText = "Hello, World!";

    private final String encryptedText = "encryptedText";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cipherService = new CipherService("testSecret", aesCipherService);
    }

    @Nested
    @DisplayName("Test class for encrypt scenarios")
    class EncryptTest {
        @Test
        @DisplayName("Test encrypt with secret key")
        void given_whenEncryptWithSecretKey_thenAssertBody() throws Exception {
            // Given
            when(aesCipherService.encrypt(anyString(), anyString())).thenReturn(encryptedText);
            // When
            String result = cipherService.encrypt(plainText, secretKey);
            // Then
            assertEquals(encryptedText, result);
            verify(aesCipherService, times(1)).encrypt(plainText, secretKey);
        }

        @Test
        @DisplayName("Test encrypt with app secret")
        void given_whenEncryptWithAppSecret_thenAssertBody() throws Exception {
            // Given
            when(aesCipherService.encrypt(anyString(), anyString())).thenReturn(encryptedText);
            // When
            String result = cipherService.encrypt(plainText);
            // Then
            assertEquals(encryptedText, result);
            verify(aesCipherService, times(1)).encrypt(plainText, "testSecret");
        }

        @Test
        @DisplayName("Encrypting error test with secret key")
        public void given_whenEncryptWithSecretKey_thenThrowCipherException() throws Exception {
            // Given
            when(aesCipherService.encrypt(plainText, secretKey)).thenThrow(new CipherException());
            // When
            Executable executable = () -> cipherService.encrypt(plainText, secretKey);
            // Then
            assertThrows(CipherException.class, executable);
        }
    }

    @Nested
    @DisplayName("Test class for decrypt scenarios")
    class DecryptTest {
        @Test
        @DisplayName("Test decrypt with secret key")
        void given_whenDecryptWithSecretKey_thenAssertBody() throws Exception {
            // Given
            when(aesCipherService.decrypt(anyString(), anyString())).thenReturn(plainText);
            // When
            String result = cipherService.decrypt(encryptedText, secretKey);
            // Then
            assertEquals(plainText, result);
            verify(aesCipherService, times(1)).decrypt(encryptedText, secretKey);
        }

        @Test
        @DisplayName("Test decrypt with app secret")
        void given_whenDecryptWithAppSecret_thenAssertBody() throws Exception {
            // Given
            when(aesCipherService.decrypt(anyString(), anyString())).thenReturn(plainText);
            // When
            String result = cipherService.decrypt(encryptedText);
            // Then
            assertEquals(plainText, result);
            verify(aesCipherService, times(1)).decrypt(encryptedText, "testSecret");
        }

        @Test
        @DisplayName("Decrypting error test with secret key")
        public void given_whenEncryptWithSecretKey_thenThrowCipherException() throws Exception {
            // Given
            when(aesCipherService.decrypt(anyString(), anyString())).thenThrow(new CipherException());
            // When
            Executable executable = () -> cipherService.decrypt(encryptedText, secretKey);
            // Then
            assertThrows(CipherException.class, executable);
        }
    }
}
