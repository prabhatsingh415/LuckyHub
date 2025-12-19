package com.LuckyHub.Backend.exception;

public class PasswordMismatchException extends RuntimeException{
    public PasswordMismatchException(String msg){
        super(msg);
    }
}
