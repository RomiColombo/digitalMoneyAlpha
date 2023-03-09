package com.digitalAlpha.cards.repository;

import com.digitalAlpha.cards.configuration.feign.OAuthFeignConfiguration;
import com.digitalAlpha.cards.model.dto.AccountDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@SuppressWarnings("rawtypes")
@ReactiveFeignClient( name = "accounts", url = "${feign.accounts.url}", configuration = OAuthFeignConfiguration.class)
public interface IAccountsFeignRepository {

    @GetMapping("accounts/{id}")
    Mono<AccountDTO> findAccountsById(@PathVariable("id") String id);

    @GetMapping("accounts/user/{userId}")
    Flux<AccountDTO> findAccountsByUserId(@PathVariable String userId);
}