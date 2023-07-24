package com.mewebstudio.javaspringbootboilerplate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class ExpectationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ExpectationException() {
        super("Expectation exception!");
    }

    public ExpectationException(final String message) {
        super(message);
    }
}
