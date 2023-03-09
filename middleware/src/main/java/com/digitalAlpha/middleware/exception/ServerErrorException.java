package com.digitalAlpha.middleware.exception;

public class ServerErrorException extends RuntimeException{

    public ServerErrorException(String message){
        super(message);
    }
}
