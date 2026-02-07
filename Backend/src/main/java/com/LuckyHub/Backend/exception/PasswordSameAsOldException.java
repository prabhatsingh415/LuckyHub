package com.LuckyHub.Backend.exception;

public class PasswordSameAsOldException extends RuntimeException {
    public PasswordSameAsOldException(String message) {
        super(message);
    }
}
