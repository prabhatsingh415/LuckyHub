package com.LuckyHub.Backend.exception;

public class RefreshTokenNotFound extends RuntimeException {
  public RefreshTokenNotFound(String message) {
    super(message);
  }
}
