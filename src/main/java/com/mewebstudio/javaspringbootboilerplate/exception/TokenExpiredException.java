package com.mewebstudio.javaspringbootboilerplate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super("Token is expired!");
    }

    public TokenExpiredException(final String message) {
        super(message);
    }
}
