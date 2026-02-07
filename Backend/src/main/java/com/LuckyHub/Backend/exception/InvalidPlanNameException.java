package com.LuckyHub.Backend.exception;

public class InvalidPlanNameException extends RuntimeException {
  public InvalidPlanNameException(String message) {
    super(message);
  }
}
