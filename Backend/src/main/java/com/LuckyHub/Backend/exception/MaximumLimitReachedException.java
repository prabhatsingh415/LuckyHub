package com.LuckyHub.Backend.exception;

public class MaximumLimitReachedException extends RuntimeException{
    public MaximumLimitReachedException(String message){super(message);}
}
