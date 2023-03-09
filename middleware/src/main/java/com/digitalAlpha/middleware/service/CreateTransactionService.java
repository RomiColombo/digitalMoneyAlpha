package com.digitalAlpha.middleware.service;

import com.digitalAlpha.middleware.exception.*;
import com.digitalAlpha.middleware.feign.IAccountsFeignRepository;
import com.digitalAlpha.middleware.feign.ITransactionFeignRepository;
import com.digitalAlpha.middleware.model.KafkaTransactionRequestDTO;
import com.digitalAlpha.middleware.model.TransactionRequestDTO;
import com.digitalAlpha.middleware.model.accounts.AccountDTO;
import com.digitalAlpha.middleware.model.enums.TransactionStatus;
import com.digitalAlpha.middleware.service.utils.CustomMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
@Slf4j
public class CreateTransactionService {


    public static final double FEE = 1.05;
    private final KafkaService kafkaService;
    private final IAccountsFeignRepository accountsFeignRepository;
    private final ITransactionFeignRepository transactionFeignRepository;
    private final ReactiveCircuitBreakerFactory cbFactory;

    private final Duration DELAY = Duration.ofMillis(50);
    public Mono<TransactionStatus> execute(TransactionRequestDTO transactionRequestDTO) {

        if (transactionRequestDTO.getAmount() < 1) throw new InvalidTransaction(CustomMessages.INVALID_AMOUNT.getMessage());
        checkRequiredPayloadFields(transactionRequestDTO);

        checkValidAccountTo(transactionRequestDTO.getAccountTo());

        Mono<Double> amount = accountsFeignRepository.getAmountByCVU(transactionRequestDTO.getAccountFrom())
                .transform(it -> cbFactory.create("getAmountByCVU")
                        .run(it, throwable -> {
                            if(throwable.getMessage().contains("404")){
                                throw new InvalidTransaction(throwable.getMessage());
                            }
                            if(throwable != null){
                                log.error(throwable.getMessage());
                                throw new ServerErrorException("The account service is unavailable at the moment. Please retry later");
                            }
                            return it;
                        }))
                .doOnNext(a -> {
                    if (a < (transactionRequestDTO.getAmount() * FEE)) throw new InsufficientFounds(CustomMessages.INVALID_AMOUNT.getMessageWithParams(a.toString(), "greater than account balance plus fee value"));
                });


        Mono<AccountDTO> accountTo = amount.flatMap(t -> {
            if (transactionRequestDTO.getAccountTo().containsKey("alias")) {
                String alias = transactionRequestDTO.getAccountTo().get("alias");
                return accountsFeignRepository.findAccountAlias(alias);
            } else {
                String cvu = transactionRequestDTO.getAccountTo().get("cvu");
                return accountsFeignRepository.findAccountCVU(cvu);
            }
        });

        Mono<String> transactionKey = accountTo.flatMap(acc -> {
            String key = randomKey();
            KafkaTransactionRequestDTO tra = KafkaTransactionRequestDTO.builder()
                    .cvuFrom(transactionRequestDTO.getAccountFrom())
                    .cvuTo(acc.getUniqueIdentifier())
                    .description(transactionRequestDTO.getDescription())
                    .userId(transactionRequestDTO.getUserId())
                    .userIdTo(acc.getUserId())
                    .amount(transactionRequestDTO.getAmount())
                    .build();
            kafkaService.publishTransaction(key, tra);
            return Mono.just(key);
        });

        return transactionKey.delayElement(DELAY)
                .flatMap(k ->
                    Flux.range(1, 5)
                            .flatMap(range -> transactionFeignRepository.getStatus(k)
                                    .transform(it -> cbFactory.create("getStatus")
                                            .run(it, throwable -> Mono.just(TransactionStatus.PENDING)))
                                    .delaySubscription(DELAY))
                            .<TransactionStatus>handle((status, sink) -> {
                                sink.next(status);
                                if (status.equals(TransactionStatus.FAIL)||status.equals(TransactionStatus.APPROVED)) sink.complete();
                            }).last());
    }

    private String randomKey() {
        return UUID.randomUUID().toString();
    }

    private void checkRequiredPayloadFields(TransactionRequestDTO transactionRequestDTO) {
        if (transactionRequestDTO.getAmount() == null ||
                transactionRequestDTO.getAccountTo() == null ||
                transactionRequestDTO.getAccountFrom() == null ||
                transactionRequestDTO.getUserId() == null ||
                transactionRequestDTO.getType() == null) {
            throw new EmptyRequiredField("All parameters are required.");
        }
    }

    private void checkValidAccountTo(Map<String, String> accountTo) {
        if (!accountTo.containsKey("alias") && !accountTo.containsKey("cvu")) {
            throw new BadFieldException(CustomMessages.INVALID_ACCOUNT_TO.getMessage());
        }
    }

}
