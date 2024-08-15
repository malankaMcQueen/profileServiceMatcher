package com.example.matcher.profileservice.exception;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException(final String msg) {
        super(msg);
    }

}
