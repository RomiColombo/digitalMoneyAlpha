package com.digitalAlpha.users.repository;
import com.digitalAlpha.users.configuration.feign.OAuthFeignConfiguration;
import com.digitalAlpha.users.model.dto.TransactionDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;

import java.util.Map;

@ReactiveFeignClient(name = "ms-transfers", url = "${feign.transactions.url}", configuration = OAuthFeignConfiguration.class)
public interface ITransfersFeignRepository {

    @GetMapping("/transactions/users/{userId}")
    Flux<TransactionDTO> getTransactionsByUserId(@PathVariable String userId , @RequestParam Map<String,String> params);
}
