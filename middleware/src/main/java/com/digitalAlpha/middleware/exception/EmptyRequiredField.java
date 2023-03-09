package com.digitalAlpha.middleware.exception;

public class EmptyRequiredField extends BusinessException{
    public EmptyRequiredField(String message){
        super(message);
    }
}
