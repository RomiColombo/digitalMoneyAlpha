package com.digitalAlpha.cards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EmptyRequiredField extends BusinessException{
    public EmptyRequiredField(String message){
        super(message);
    }
}
