package com.digitalAlpha.middleware.feign;

import com.digitalAlpha.middleware.configuration.feign.OAuthFeignConfiguration;
import com.digitalAlpha.middleware.model.ReducedTransactionDTO;
import com.digitalAlpha.middleware.model.TransactionDTO;
import com.digitalAlpha.middleware.model.enums.TransactionStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@ReactiveFeignClient( name = "transactions", url = "${feign.transactions.url}", configuration = OAuthFeignConfiguration.class)
public interface ITransactionFeignRepository {

    @GetMapping("/transactions/status/{id}")
    Mono<TransactionStatus> getStatus(@PathVariable String id);

    @GetMapping("/transactions/{id}")
    Mono<TransactionDTO> getById(@PathVariable String id);

    @GetMapping("/transactions/users/{userId}")
    Flux<ReducedTransactionDTO> getTransactionsByUserId(@PathVariable String userId , @RequestParam Map<String,String> params);
}
