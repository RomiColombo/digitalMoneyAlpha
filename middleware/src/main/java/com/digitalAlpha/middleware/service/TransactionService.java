package com.digitalAlpha.middleware.service;

import com.digitalAlpha.middleware.exception.ResourceNotFound;
import com.digitalAlpha.middleware.exception.ServerErrorException;
import com.digitalAlpha.middleware.feign.ITransactionFeignRepository;
import com.digitalAlpha.middleware.model.ReducedTransactionDTO;
import com.digitalAlpha.middleware.model.TransactionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final ITransactionFeignRepository transactionFeignRepository;

    private final ReactiveCircuitBreakerFactory cbFactory;

    public Mono<TransactionDTO> getById(String id){
        return transactionFeignRepository.getById(id).doOnSuccess(t -> {
            if(t == null) throw new ResourceNotFound("Can't find transaction");
        }).transform(it -> cbFactory.create("getById")
                .run(it, throwable -> {
                    throw new ServerErrorException("The transaction service is unavailable at the moment. Please retry later");
                }));
    }

    public Flux<ReducedTransactionDTO> getTransactionsByUserId(String id, Map<String,String> params){
        return transactionFeignRepository.getTransactionsByUserId(id,params)
                .transform(it -> cbFactory.create("getTransactionsByUserId")
                        .run(it, throwable -> {
                            throw new ServerErrorException("The transaction service is unavailable at the moment. Please retry later");
                        }));
    }

}
