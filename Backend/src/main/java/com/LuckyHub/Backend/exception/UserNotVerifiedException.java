package com.LuckyHub.Backend.exception;

public class UserNotVerifiedException extends RuntimeException {
    private final String email;
    private final String token;

    public UserNotVerifiedException(String message, String email, String token) {
        super(message);
        this.email = email;
        this.token = token;
    }

    // Getters
    public String getEmail() { return email; }
    public String getToken() { return token; }
}