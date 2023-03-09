package com.digitalAlpha.transactions.exception;

public class EmptyRequiredField extends BusinessException{
    public EmptyRequiredField(String message){
        super(message);
    }
}
