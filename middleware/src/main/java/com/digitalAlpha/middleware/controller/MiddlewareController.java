package com.digitalAlpha.middleware.controller;

import com.digitalAlpha.middleware.exception.EmptyRequiredField;
import com.digitalAlpha.middleware.exception.InvalidTransaction;
import com.digitalAlpha.middleware.exception.ResourceNotFound;
import com.digitalAlpha.middleware.model.DepositDTO;
import com.digitalAlpha.middleware.model.ReducedTransactionDTO;
import com.digitalAlpha.middleware.model.TransactionDTO;
import com.digitalAlpha.middleware.model.TransactionRequestDTO;
import com.digitalAlpha.middleware.model.enums.TransactionStatus;
import com.digitalAlpha.middleware.service.CreateDepositService;
import com.digitalAlpha.middleware.service.CreateTransactionService;
import com.digitalAlpha.middleware.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/transactions")
public class MiddlewareController {
    private final CreateDepositService createDepositService;
    private final CreateTransactionService createTransactionService;
    private final TransactionService  transactionService;

    @PostMapping("/deposit")
    @PreAuthorize("authentication.name == #depositDTO.userId || hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Mono<TransactionStatus> createDeposit(@RequestBody DepositDTO depositDTO, Principal principal) throws InvalidTransaction {
        return createDepositService.execute(depositDTO,principal);
    }

    @PostMapping
    @PreAuthorize("authentication.name == #transactionRequestDTO.userId || hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Mono<TransactionStatus> createTransaction(@RequestBody TransactionRequestDTO transactionRequestDTO){
        return createTransactionService.execute(transactionRequestDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("authentication.name == #returnObject.userId || hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Mono<TransactionDTO> getById(@PathVariable String id) throws EmptyRequiredField, ResourceNotFound {
        return transactionService.getById(id);
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("authentication.name == #userId || hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Flux<ReducedTransactionDTO> getTransactionsByUserId(@PathVariable String userId, @RequestParam Map<String,String> params){
        return transactionService.getTransactionsByUserId(userId,params);
    }
}
