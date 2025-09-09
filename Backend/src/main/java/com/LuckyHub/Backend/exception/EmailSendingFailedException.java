package com.LuckyHub.Backend.exception;

public class EmailSendingFailedException extends RuntimeException {
    public EmailSendingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}