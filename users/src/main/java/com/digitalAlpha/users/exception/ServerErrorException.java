package com.digitalAlpha.users.exception;

public class ServerErrorException extends RuntimeException{

    public ServerErrorException(String message){
        super(message);
    }
}
