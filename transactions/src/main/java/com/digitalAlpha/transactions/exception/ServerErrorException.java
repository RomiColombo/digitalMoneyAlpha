package com.digitalAlpha.transactions.exception;

public class ServerErrorException extends RuntimeException{

    public ServerErrorException(String message){
        super(message);
    }
}
