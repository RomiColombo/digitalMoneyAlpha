package com.digitalAlpha.middleware.service;

import com.digitalAlpha.middleware.exception.*;
import com.digitalAlpha.middleware.feign.IAccountsFeignRepository;
import com.digitalAlpha.middleware.feign.ITransactionFeignRepository;
import com.digitalAlpha.middleware.model.DepositDTO;
import com.digitalAlpha.middleware.model.accounts.AccountDTO;
import com.digitalAlpha.middleware.model.enums.TransactionStatus;
import com.digitalAlpha.middleware.service.utils.CustomMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("rawtypes")
public class CreateDepositService {

    private final KafkaService kafkaService;
    private final IAccountsFeignRepository accountsFeignRepository;
    private final ITransactionFeignRepository transactionFeignRepository;
    private final ReactiveCircuitBreakerFactory cbFactory;
    private final Duration DELAY = Duration.ofMillis(100);

    public Mono<TransactionStatus> execute(DepositDTO depositDTO, Principal principal) {

        if (depositDTO.getAmount() < 1) throw new InvalidTransaction(CustomMessages.INVALID_AMOUNT.getMessage());
        checkRequiredPayloadFields(depositDTO);

        Mono<AccountDTO> account = accountsFeignRepository.findAccountsById(depositDTO.getAccountId())
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
                    if(!a.getUserId().equals(depositDTO.getUserId())) {
                        throw new BadFieldException("Account does not belong to authenticated user");
                    }
                });

        Mono<String> depositKey = account.map(a -> {
            String key = randomKey();
            depositDTO.setUserId(principal.getName());
            kafkaService.publishDeposit(key, depositDTO);
            return key;
        });

        return depositKey.delayElement(DELAY)
                .flatMap(k ->
                        Flux.range(1, 5)
                                .flatMap(range -> transactionFeignRepository.getStatus(k)
                                        .transform(it -> cbFactory.create("getStatus")
                                                .run(it, throwable -> Mono.just(TransactionStatus.PENDING)))
                                        .delaySubscription(Duration.ofMillis(10)))
                                .<TransactionStatus>handle((status, sink) -> {
                                    sink.next(status);
                                    if (status.equals(TransactionStatus.FAIL)||status.equals(TransactionStatus.APPROVED)) sink.complete();
                                }).last());
    }

    private String randomKey() {
        return UUID.randomUUID().toString();
    }

    private void checkRequiredPayloadFields(DepositDTO depositDTO) {
        if (depositDTO.getAccountId() == null ||
                depositDTO.getAmount() == null ||
                depositDTO.getCardNumber() == null) {
            throw new EmptyRequiredField("All parameters are required.");
        }
    }

}
