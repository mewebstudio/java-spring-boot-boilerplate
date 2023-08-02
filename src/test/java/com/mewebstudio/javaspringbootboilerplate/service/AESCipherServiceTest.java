package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
@DisplayName("Unit tests for AESCipherService")
class AESCipherServiceTest {
    @Test
    @DisplayName("Test class for encryption and decryption scenarios")
    public void testEncryptAndDecrypt() throws Exception {
        String plainText = "Hello, world!";

        AESCipherService aesCipherWrapper = new AESCipherService();

        String encryptedText = aesCipherWrapper.encrypt(plainText, Constants.APP_SECRET_KEY);
        String decryptedText = aesCipherWrapper.decrypt(encryptedText, Constants.APP_SECRET_KEY);

        assertEquals(plainText, decryptedText);
    }
}
