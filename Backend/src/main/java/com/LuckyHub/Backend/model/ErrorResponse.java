package com.LuckyHub.Backend.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private String status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
}