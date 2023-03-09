package com.digitalAlpha.middleware.exception.handler;

import com.digitalAlpha.middleware.exception.*;
import com.digitalAlpha.middleware.exception.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ServerWebExchange;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> allErrors(Exception ex, ServerWebExchange web){
        log.error("General error["+ex.getMessage()+"] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<Error> ResourceNotFound(ResourceNotFound ex, ServerWebExchange web) {
        log.error("Not Found: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .status(HttpStatus.NOT_FOUND)
                .message("Error: "+ ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmptyRequiredField.class)
    public ResponseEntity<Error> EmptyRequiredField(EmptyRequiredField ex, ServerWebExchange web) {
        log.error("Empty required: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message("Empty required: "+ ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BadFieldException.class)
    public ResponseEntity<Error> BadField(BadFieldException ex, ServerWebExchange web) {
        log.error("Bad field: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message("Bad field: "+ ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    @ExceptionHandler(InvalidTransaction.class)
    public ResponseEntity<Error> InvalidTransaction(InvalidTransaction ex, ServerWebExchange web) {
        log.error("Invalid transaction: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message("Invalid transaction: "+ ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InsufficientFounds.class)
    public ResponseEntity<Error> InsufficientFounds(InsufficientFounds ex, ServerWebExchange web) {
        log.error("Insufficient Founds: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.GONE.value())
                .status(HttpStatus.GONE)
                .message("Insufficient Founds: "+ ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.GONE).body(error);
    }

}
