package com.digitalAlpha.accounts.exception;

public class EmptyRequiredField extends BusinessException{
    public EmptyRequiredField(String message){
        super(message);
    }
}
