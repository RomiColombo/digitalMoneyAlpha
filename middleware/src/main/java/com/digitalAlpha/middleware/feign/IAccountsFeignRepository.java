package com.digitalAlpha.middleware.feign;

import com.digitalAlpha.middleware.configuration.feign.OAuthFeignConfiguration;
import com.digitalAlpha.middleware.model.accounts.AccountDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;


@SuppressWarnings("rawtypes")
@ReactiveFeignClient( name = "accounts", url = "${feign.accounts.url}", configuration = OAuthFeignConfiguration.class)
public interface IAccountsFeignRepository {

    @GetMapping("accounts/{id}")
    Mono<AccountDTO> findAccountsById(@PathVariable("id") String id);

    @GetMapping("accounts/alias/{alias}")
    Mono<AccountDTO> findAccountAlias(@PathVariable String alias);

    @GetMapping("accounts/cvu/{cvu}")
    Mono<AccountDTO> findAccountCVU(@PathVariable String cvu);

    @GetMapping("accounts/amount/{cvu}")
    Mono<Double> getAmountByCVU(@PathVariable String cvu);

}