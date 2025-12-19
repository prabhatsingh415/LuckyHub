package com.LuckyHub.Backend.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Duplicate entry detected. Please check your input.";
        if (ex.getCause() != null) {
            message = Objects.requireNonNull(ex.getRootCause()).getMessage();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "status", "error",
                        "message", message
                ));
    }

    @ExceptionHandler(VideosFromDifferentChannelsException.class)
    public ResponseEntity<?> handleVideosFromDifferentChannels(VideosFromDifferentChannelsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", "error",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(PlanLimitExceedException.class)
    public ResponseEntity<Map<String, String>> handlePlanLimitExceed(PlanLimitExceedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Plan Limit Exceeded",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(PlansGiveawayLimitExceedException.class)
    public ResponseEntity<Map<String, String>> handlePlansGiveawayLimitExceed(PlansGiveawayLimitExceedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Giveaway Limit Exceeded",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(JWTTokenNotFoundOrInvalidException.class)
    public ResponseEntity<Map<String, String>> handleJWTTokenException(JWTTokenNotFoundOrInvalidException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "error", "Invalid or Missing JWT Token",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(InvalidAmountForAnyPlanException.class)
    public ResponseEntity<Map<String, String>> handleInvalidAmountException(InvalidAmountForAnyPlanException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Invalid Plan Amount !",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleInvalidAmountException(PaymentNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", "Payment Data Not Found !",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(MaximumLimitReachedException.class)
    public ResponseEntity<Map<String, String>> handleMaximumLimitReachedException(MaximumLimitReachedException ex){
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of(
                        "error", "Maximum Limit Reached!",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTokenException(InvalidTokenException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Invalid Verification token!",
                        "message", ex.getMessage()
                ));
    }

    public ResponseEntity<Map<String, String>> handlesImageUploadExceptionFailed(ImageUploadFailedException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Internal error !",
                        "message", ex.getMessage()
                ));
    }

    public ResponseEntity<Map<String, String>> handlePasswordMismatchException(PasswordMismatchException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Internal error !",
                        "message", ex.getMessage()
                ));
    }

    public ResponseEntity<Map<String, String>> handleInvalidCurrentPasswordException(InvalidCurrentPasswordException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Internal error !",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Something went wrong",
                        "message", ex.getMessage()
                ));
    }

}
