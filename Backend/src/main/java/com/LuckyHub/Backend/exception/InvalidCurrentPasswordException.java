package com.LuckyHub.Backend.exception;

public class InvalidCurrentPasswordException extends RuntimeException{
    public InvalidCurrentPasswordException(String msg){
        super(msg);
    }
}
