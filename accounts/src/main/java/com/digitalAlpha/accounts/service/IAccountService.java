package com.digitalAlpha.accounts.service;

import com.digitalAlpha.accounts.exception.*;
import com.digitalAlpha.accounts.model.Account;
import com.digitalAlpha.accounts.model.AccountRequestDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.rmi.ServerException;
import java.security.Principal;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("rawtypes")
public interface IAccountService {
    Flux<Account> getByUserId(String userId) throws ResourceNotFound;
    Mono<Account<?>> save(String userId, String type) throws AlreadyExist, EmptyRequiredField, IOException;
    Mono<Account> getById(String id) throws ResourceNotFound, EmptyRequiredField;

    Mono<Double> getAmountByCVU(String cvu) throws ResourceNotFound, EmptyRequiredField;

    Mono<Void>  invalidate(String id, Principal principal) throws ServerException, EmptyRequiredField, ResourceNotFound, ServerErrorException, ExecutionException, InterruptedException;
    Mono<Account> updateAlias(String id, String alias,String userId) throws AlreadyExist, ResourceNotFound, BadField, EmptyRequiredField, ServerErrorException, ExecutionException, InterruptedException;
    Mono<Boolean> updateAmount(String id, AccountRequestDTO transaction) throws BadField, ResourceNotFound, EmptyRequiredField;

    Mono<Account> getByAlias(String alias) throws ResourceNotFound, ServerErrorException, EmptyRequiredField;

    Mono<Account> getByCVU(String cvu) throws ResourceNotFound, ServerErrorException, EmptyRequiredField;

    Mono<Boolean> validateAccountByUserId(String accountId, String userId) throws ResourceNotFound;

}
