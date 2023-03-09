package com.digitalAlpha.middleware.exception;

public class InvalidTransaction extends BusinessException{

    public InvalidTransaction(String message){
        super(message);
    }
}
