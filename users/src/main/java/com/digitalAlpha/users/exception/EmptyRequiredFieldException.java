package com.digitalAlpha.users.exception;

public class EmptyRequiredFieldException extends BusinessException{
    public EmptyRequiredFieldException(String message){
        super(message);
    }
}
