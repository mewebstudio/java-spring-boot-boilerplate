package com.mewebstudio.javaspringbootboilerplate.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
@DisplayName("Unit tests for RandomStringGenerator")
public class RandomStringGeneratorTest {
    @Test
    public void testNextWithDefaultSymbols() {
        // Given
        int length = 10;
        Random random = new SecureRandom();
        RandomStringGenerator generator = new RandomStringGenerator(length, random);
        // When
        String randomString = generator.next();
        // Then
        assertNotNull(randomString);
        assertEquals(length, randomString.length());
    }

    @Test
    public void testNextWithCustomSymbols() {
        // Given
        int length = 8;
        Random random = new SecureRandom();
        String customSymbols = "abc123";
        RandomStringGenerator generator = new RandomStringGenerator(length, random, customSymbols);
        // When
        String randomString = generator.next();
        // Then
        assertNotNull(randomString);
        assertEquals(length, randomString.length());
        assertTrue(randomString.matches("[" + customSymbols + "]+"));
    }

    @Test
    public void testNextWithOnlyDigits() {
        // Given
        int length = 6;
        Random random = new SecureRandom();
        RandomStringGenerator generator = new RandomStringGenerator(length, random, "0123456789");
        // When
        String randomString = generator.next();
        // Then
        assertNotNull(randomString);
        assertEquals(length, randomString.length());
        assertTrue(randomString.matches("[0-9]+"));
    }

    @Test
    public void testNextWithOnlyDigitsUsingConstructor() {
        // Given
        int length = 5;
        RandomStringGenerator generator = new RandomStringGenerator(length, true);
        // When
        String randomString = generator.next();
        // Then
        assertNotNull(randomString);
        assertEquals(length, randomString.length());
        assertTrue(randomString.matches("[0-9]+"));
    }

    @Test
    public void testNextWithDefaultSymbolsUsingConstructor() {
        // Given
        int length = 7;
        RandomStringGenerator generator = new RandomStringGenerator(length, false);
        // When
        String randomString = generator.next();
        // Then
        assertNotNull(randomString);
        assertEquals(length, randomString.length());
        assertTrue(randomString.matches("[A-Za-z0-9]+"));
    }
}
