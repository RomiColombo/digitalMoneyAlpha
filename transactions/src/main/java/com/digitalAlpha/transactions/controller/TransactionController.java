package com.digitalAlpha.transactions.controller;

import com.digitalAlpha.transactions.exception.EmptyRequiredField;
import com.digitalAlpha.transactions.exception.ResourceNotFound;
import com.digitalAlpha.transactions.model.dto.*;
import com.digitalAlpha.transactions.model.enums.TransactionStatus;
import com.digitalAlpha.transactions.service.ITransactionService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private ITransactionService service;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Mono<TransactionDTO> getById(@PathVariable String id) throws EmptyRequiredField, ResourceNotFound {
        return service.getById(id);
    }

    @GetMapping("/status/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Mono<TransactionStatus> getStatusById(@PathVariable String id) {
        return service.getStatus(id);
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Flux<ReducedTransactionDTO> getTransactionsByUserId(@PathVariable String userId, @RequestParam Map<String,String> params){
        return service.getTransactionsByUserId(userId,params);
    }
}
