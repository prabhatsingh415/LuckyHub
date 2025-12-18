package com.LuckyHub.Backend.exception;

public class ImageUploadFailedException extends RuntimeException{
   public ImageUploadFailedException(String msg){
       super(msg);
   }
}
