package com.digitalAlpha.transactions.service;

import com.digitalAlpha.transactions.exception.EmptyRequiredField;
import com.digitalAlpha.transactions.exception.ResourceNotFound;
import com.digitalAlpha.transactions.model.dto.*;
import com.digitalAlpha.transactions.exception.*;
import com.digitalAlpha.transactions.model.enums.TransactionStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ITransactionService {

    Mono<TransactionDTO> getById(String id) throws EmptyRequiredField, ResourceNotFound;

    Flux<ReducedTransactionDTO> getTransactionsByUserId(String id, Map<String,String> params);

    Mono<Void> createTransaction(PublishTransactionDTO publish) throws EmptyRequiredField, InvalidTransaction;

    Mono<Void> createDeposit(PublishTransactionDTO publish) throws InvalidTransaction, ResourceNotFound, EmptyRequiredField, ServerErrorException;

    Mono<TransactionStatus> getStatus(String id);

}
