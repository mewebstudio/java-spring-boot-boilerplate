package com.mewebstudio.javaspringbootboilerplate.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class AESCipher {
    private static final String ALGORITHM = "AES";

    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    private AESCipher() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated!");
    }

    /**
     * Encrypts plain text using AES algorithm.
     *
     * @param plainText The text that will be encrypted.
     * @param secretKey The key (256 bit) that will be used for encryption.
     * @return Base64 encoded string of the encrypted text.
     */
    public static String encrypt(String plainText, String secretKey) throws Exception {
        SecretKeySpec keySpec = generateKey(secretKey);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypts encrypted text using AES algorithm.
     *
     * @param encryptedText The text that will be decrypted.
     * @param secretKey     The key (256 bit) that will be used for decryption.
     * @return String decrypted text.
     */
    public static String decrypt(String encryptedText, String secretKey) throws Exception {
        SecretKeySpec keySpec = generateKey(secretKey);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Generates a secret key to be used for encryption and decryption.
     *
     * @param secretKey The key that will be used for encryption and decryption.
     * @return SecretKeySpec
     */
    private static SecretKeySpec generateKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
}
