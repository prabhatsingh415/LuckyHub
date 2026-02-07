package com.LuckyHub.Backend.exception;

public class RequestTooEarlyException extends RuntimeException {
  public RequestTooEarlyException(String message) {
    super(message);
  }
}
