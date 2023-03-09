package com.digitalAlpha.transactions.repository;

import com.digitalAlpha.transactions.exception.BadField;
import com.digitalAlpha.transactions.exception.EmptyRequiredField;
import com.digitalAlpha.transactions.exception.ResourceNotFound;
import com.digitalAlpha.transactions.model.Transaction;
import com.digitalAlpha.transactions.model.dto.ReducedTransactionDTO;
import com.digitalAlpha.transactions.model.enums.TransactionStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;


public interface ITransactionRepository {

    Mono<Transaction> createTransaction(Transaction transaction) throws EmptyRequiredField;

    Mono<Transaction> getById(String id) throws ResourceNotFound;

    Flux<ReducedTransactionDTO> getByFilters(Map<String, Object> filters, String userId) throws BadField;

    Mono<Transaction> updateStatus(String id, TransactionStatus transactionStatus) throws ResourceNotFound;

    Mono<TransactionStatus> getStatus(String id);
}
