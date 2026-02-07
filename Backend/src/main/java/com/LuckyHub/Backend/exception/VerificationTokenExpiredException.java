package com.LuckyHub.Backend.exception;

public class VerificationTokenExpiredException extends RuntimeException {
  public VerificationTokenExpiredException(String message) {
    super(message);
  }
}
