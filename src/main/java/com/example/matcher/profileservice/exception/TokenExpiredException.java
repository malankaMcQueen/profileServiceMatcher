package com.example.matcher.profileservice.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(final String msg) {
        super(msg);
    }
}

