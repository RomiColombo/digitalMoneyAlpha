package com.digitalAlpha.accounts.exception;

public class ServerErrorException extends RuntimeException{

    public ServerErrorException(String message){
        super(message);
    }
}
