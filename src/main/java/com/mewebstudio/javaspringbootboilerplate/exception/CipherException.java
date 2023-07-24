package com.mewebstudio.javaspringbootboilerplate.exception;

import java.io.Serial;

public class CipherException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public CipherException() {
        super("Cipher exception!");
    }

    public CipherException(final String message) {
        super(message);
    }

    public CipherException(String message, Throwable cause) {
        super(message, cause);
    }

    public CipherException(Throwable cause) {
        super(cause);
    }

    protected CipherException(String message,
                              Throwable cause,
                              boolean enableSuppression,
                              boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
