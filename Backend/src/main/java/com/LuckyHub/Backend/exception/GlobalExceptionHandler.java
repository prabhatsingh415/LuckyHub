package com.LuckyHub.Backend.exception;

import com.LuckyHub.Backend.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error, String message) {
        ErrorResponse response = ErrorResponse.builder()
                .status("error")
                .error(error)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            UserEmailNotFoundException.class,
            PaymentNotFoundException.class,
            VerificationTokenNotFoundException.class,
            RefreshTokenNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(Exception ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    @ExceptionHandler({
            UsernameNotFoundException.class,
            JWTTokenNotFoundOrInvalidException.class,
            InvalidTokenException.class,
            GoogleAuthenticationFailedException.class,
            UnauthorizedException.class
    })
    public ResponseEntity<ErrorResponse> handleUnauthorized(Exception ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage());
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<?> handleUserNotVerified(UserNotVerifiedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "status", "UNVERIFIED",
                "message", ex.getMessage(),
                "email", ex.getEmail(),
                "token", ex.getToken()
        ));
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleConflict(DataIntegrityViolationException ex) {
        String message = (ex.getRootCause() != null) ? ex.getRootCause().getMessage() : "Duplicate entry detected";
        return buildResponse(HttpStatus.CONFLICT, "Conflict", message);
    }

    @ExceptionHandler({MaximumLimitReachedException.class, RequestTooEarlyException.class})
    public ResponseEntity<ErrorResponse> handleLimits(Exception ex) {
        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, "Limit Exceeded", ex.getMessage());
    }


    @ExceptionHandler({
            VideosFromDifferentChannelsException.class, PlanLimitExceedException.class,
            PlansGiveawayLimitExceedException.class, InvalidAmountForAnyPlanException.class,
            PasswordMismatchException.class, UserAlreadyExistsException.class,
            InvalidOTPException.class, SubscriptionDowngradeException.class,
            PaymentGatewayException.class, ImageUploadFailedException.class,
            InvalidCurrentPasswordException.class, UserIsAlreadyVerifiedException.class,
            VerificationTokenExpiredException.class, PasswordTokenExpiredException.class,
            PasswordSameAsOldException.class, InvalidPlanNameException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(org.springframework.web.client.RestClientException.class)
    public ResponseEntity<ErrorResponse> handleExternalApi(Exception ex) {
        return buildResponse(HttpStatus.BAD_GATEWAY, "Gateway Error", "External API communication failed.");
    }

    @ExceptionHandler(EmailSendingFailedException.class)
    public ResponseEntity<ErrorResponse> handleEmailError(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Mail Error", ex.getMessage());
    }

    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataError(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Data Integrity Error", "Multiple records found where one was expected.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("CRITICAL ERROR: ", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", "An unexpected error occurred.");
    }
}