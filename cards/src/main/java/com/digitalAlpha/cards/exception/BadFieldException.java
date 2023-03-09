package com.digitalAlpha.cards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadFieldException extends BusinessException{
    public BadFieldException(String message){
        super(message);
    }
}
