package com.digitalAlpha.cards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AlreadyExist extends BusinessException{
    public AlreadyExist(String message){
        super(message);
    }
}
