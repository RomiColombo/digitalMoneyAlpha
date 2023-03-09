package com.digitalAlpha.users.exception;

public class AlreadyExistException extends BusinessException{
    public AlreadyExistException(String message){
        super(message);
    }
}
