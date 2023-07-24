package com.mewebstudio.javaspringbootboilerplate.util;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class RandomStringGenerator {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String LOWER = UPPER.toLowerCase(Locale.ROOT);

    private static final String DIGITS = "0123456789";

    private static final String ALPHA_NUM = UPPER + LOWER + DIGITS;

    private final Random random;

    private final char[] symbols;

    private final char[] buf;

    public RandomStringGenerator(int length, Random random, String symbols) {
        if (length < 1) throw new IllegalArgumentException();
        if (symbols.length() < 2) throw new IllegalArgumentException();

        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    public RandomStringGenerator(int length, Random random) {
        this(length, random, ALPHA_NUM);
    }

    public RandomStringGenerator(int length) {
        this(length, new SecureRandom());
    }

    public RandomStringGenerator(int length, boolean isDigit) {
        this(length, new SecureRandom(), isDigit ? DIGITS : ALPHA_NUM);
    }

    /**
     * Generate random string.
     *
     * @return String
     */
    public String next() {
        for (int i = 0; i < buf.length; ++i) {
            buf[i] = symbols[random.nextInt(symbols.length)];
        }

        return new String(buf);
    }
}
