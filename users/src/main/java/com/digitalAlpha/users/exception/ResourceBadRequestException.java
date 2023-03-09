package com.digitalAlpha.users.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ResourceBadRequestException extends BusinessException {
    public ResourceBadRequestException(String message) {
        super(message);
    }
}
