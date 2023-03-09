package com.digitalAlpha.users.exception.handler;

import com.digitalAlpha.users.exception.*;
import com.digitalAlpha.users.exception.Error;
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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Error> ResourceNotFound(ResourceNotFoundException ex, ServerWebExchange web) {
        log.error("Not Found: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .status(HttpStatus.NOT_FOUND)
                .message("Not Found: " + ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EmptyRequiredFieldException.class)
    public ResponseEntity<Error> EmptyRequiredField(EmptyRequiredFieldException ex, ServerWebExchange web) {
        log.error("Empty required: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message("Empty required: "+ ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<Error> AlreadyExist(AlreadyExistException ex, ServerWebExchange web) {
        log.error("Already exist: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message("Already exist:" + ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BadFieldException.class)
    public ResponseEntity<Error> BadField(BadFieldException ex, ServerWebExchange web) {
        log.error("Bad field: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message("Bad field: " + ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ServerErrorException.class)
    public ResponseEntity<Error> ServerError(ServerErrorException ex, ServerWebExchange web) {
        log.error("Server Error: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Server Error: " + ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(KeycloakErrorException.class)
    public ResponseEntity<Error> keycloakError(KeycloakErrorException ex, ServerWebExchange web) {
        log.error("Keycloak Error: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Keycloak Error: " + ex.getMessage())
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Error> conflictError(ConflictException ex, ServerWebExchange web) {
        log.error("Internal Error: [" + ex.getMessage() + "] Path: " + web.getRequest().getPath());
        Error error = Error.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ex.getMessage() + ", try again later.")
                .path(String.valueOf(web.getRequest().getPath())).build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
