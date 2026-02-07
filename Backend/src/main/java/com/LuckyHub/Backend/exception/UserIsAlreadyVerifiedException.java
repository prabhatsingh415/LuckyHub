package com.LuckyHub.Backend.exception;

public class UserIsAlreadyVerifiedException extends RuntimeException {
    public UserIsAlreadyVerifiedException(String message) {
        super(message);
    }
}
