package com.LuckyHub.Backend.exception;

public class PasswordTokenExpiredException extends RuntimeException {
    public PasswordTokenExpiredException(String message) {
        super(message);
    }
}
