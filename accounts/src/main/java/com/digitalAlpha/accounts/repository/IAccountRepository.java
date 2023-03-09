package com.digitalAlpha.accounts.repository;

import com.digitalAlpha.accounts.exception.*;
import com.digitalAlpha.accounts.model.Account;
import com.digitalAlpha.accounts.model.enums.TransactionType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("rawtypes")
public interface IAccountRepository {
    Flux<Account> findAccountsByUserId(String username) throws ResourceNotFound;

    Mono<Account> findById(String id) throws ResourceNotFound;

    Mono<Account<?>> save(Account<?> account) throws EmptyRequiredField;

    Mono<Void> invalidateById(String id) throws ServerErrorException;

    Mono<Account> updateAlias(String id, String alias) throws AlreadyExist, ResourceNotFound, BadField, ServerErrorException;

    Mono<Boolean> updateAmount(String id, Double amount, TransactionType type) throws BadField;

    Mono<Account> findByAlias(String alias) throws ResourceNotFound, ServerErrorException;

    Mono<Double> getAmountByCVU(String id) throws ResourceNotFound;

    Mono<Account> findByCVU(String cvu) throws ResourceNotFound, ServerErrorException;
}
