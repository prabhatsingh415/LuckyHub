package com.LuckyHub.Backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Something went wrong",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "error", "Unauthorized",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(org.springframework.web.client.RestClientException.class)
    public ResponseEntity<Map<String, String>> handleRestClientException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of(
                        "error", "Failed to communicate with Google OAuth servers",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", "User Not Found",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(UserEmailNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserEmailNotFoundException(UserEmailNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", "User Email Not Found",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(EmailSendingFailedException.class)
    public ResponseEntity<Map<String, String>> handleEmailSendingFailedException(EmailSendingFailedException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Email Sending Failed",
                        "message", ex.getMessage()
                ));
    }

}
