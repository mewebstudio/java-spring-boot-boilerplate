package com.mewebstudio.javaspringbootboilerplate.util;

import com.mewebstudio.javaspringbootboilerplate.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
@DisplayName("Unit tests for AESCipher")
public class AESCipherTest {
    @Test
    @DisplayName("Test class for encryption and decryption scenarios")
    public void testEncryptionAndDecryption() throws Exception {
        String plainText = "Hello, World!";

        String encryptedText = AESCipher.encrypt(plainText, Constants.APP_SECRET_KEY);
        String decryptedText = AESCipher.decrypt(encryptedText, Constants.APP_SECRET_KEY);

        assertEquals(plainText, decryptedText);
    }
}
