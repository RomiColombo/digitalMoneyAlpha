package com.digitalAlpha.cards.exception;

public class ServerErrorException extends RuntimeException{

    public ServerErrorException(String message){
        super(message);
    }
}
