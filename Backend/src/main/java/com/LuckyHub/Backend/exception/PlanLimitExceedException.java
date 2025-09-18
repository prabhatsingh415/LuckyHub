package com.LuckyHub.Backend.exception;

public class PlanLimitExceedException extends RuntimeException {
    public PlanLimitExceedException(String message) {
        super(message);
    }
}
