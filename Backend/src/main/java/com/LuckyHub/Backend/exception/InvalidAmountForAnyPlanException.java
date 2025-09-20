package com.LuckyHub.Backend.exception;

public class InvalidAmountForAnyPlanException extends RuntimeException {
    public InvalidAmountForAnyPlanException(String message) {
        super(message);
    }
}
