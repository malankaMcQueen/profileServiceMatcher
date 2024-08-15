package com.example.matcher.profileservice.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(final String msg) {
        super(msg);
    }
}
