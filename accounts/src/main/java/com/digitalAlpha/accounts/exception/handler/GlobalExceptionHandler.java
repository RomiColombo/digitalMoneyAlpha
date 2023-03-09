package com.digitalAlpha.accounts.exception.handler;

import com.digitalAlpha.accounts.exception.*;
import com.digitalAlpha.accounts.exception.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Error> allErrors(Exception ex, ServerWebExchange web){
//        log.error("General error["+ex.getMessage()+"] Path: " + web.getRequest().getPath());
//        Error error = Error.builder()
//                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .message(ex.getMessage())
//                .path(String.valueOf(web.getRequest().getPath())).build();
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<Error> ResourceNotFound(ResourceNotFound ex, ServerWebExchange web) {
        log.error("Not Found: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .status(HttpStatus.NOT_FOUND)
                .message("Not Found: "+ ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

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

    @ExceptionHandler(AlreadyExist.class)
    public ResponseEntity<Error> AlreadyExist(AlreadyExist ex, ServerWebExchange web) {
        log.error("Already Exist: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message("Already Exist: "+ ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BadField.class)
    public ResponseEntity<Error> BadField(BadField ex, ServerWebExchange web) {
        log.info("Bad field: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message("Bad field: "+ ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Error> Forbidden(AccessDeniedException ex, ServerWebExchange web) {
        log.info("Access denied: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .status(HttpStatus.FORBIDDEN)
                .message(ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
