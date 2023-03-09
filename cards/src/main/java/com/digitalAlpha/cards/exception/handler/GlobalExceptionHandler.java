package com.digitalAlpha.cards.exception.handler;

import com.digitalAlpha.cards.exception.AlreadyExist;
import com.digitalAlpha.cards.exception.BadFieldException;
import com.digitalAlpha.cards.exception.EmptyRequiredField;
import com.digitalAlpha.cards.exception.ResourceNotFound;
import com.digitalAlpha.cards.exception.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> allErrors(Exception ex, ServerWebExchange web) {
        log.info("General error[" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /* Resource not found*/
    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<Error> ResourceNotFound(ResourceNotFound ex, ServerWebExchange web) {
        log.info("Not Found: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .status(HttpStatus.NOT_FOUND)
                .message("Not Found: "+ ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /*Empty required field*/
    @ExceptionHandler(EmptyRequiredField.class)
    public ResponseEntity<Error> EmptyRequiredField(EmptyRequiredField ex, ServerWebExchange web) {
        log.info("Empty required: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message("Empty required: "+ ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /*Already exist*/
    @ExceptionHandler(AlreadyExist.class)
    public ResponseEntity<Error> AlreadyExist(AlreadyExist ex, ServerWebExchange web) {
        log.info("Already Exist: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message("Already Exist: "+ ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    /*Bad Field*/
    @ExceptionHandler(BadFieldException.class)
    public ResponseEntity<Error> BadFieldException(BadFieldException ex, ServerWebExchange web) {
        log.info("Bad Field: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message("Bad Field: "+ ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
