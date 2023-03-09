package com.digitalAlpha.transactions.repository;

import com.digitalAlpha.transactions.configuration.feign.OAuthFeignConfiguration;
import com.digitalAlpha.transactions.exception.EmptyRequiredField;
import com.digitalAlpha.transactions.exception.ResourceNotFound;
import com.digitalAlpha.transactions.exception.ServerErrorException;
import com.digitalAlpha.transactions.model.accounts.Account;
import com.digitalAlpha.transactions.model.dto.AccountRequestDTO;
import com.digitalAlpha.transactions.model.dto.UpdateAmountDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactivefeign.client.ReactiveFeignException;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@SuppressWarnings("rawtypes")
@ReactiveFeignClient(name = "ms-accounts", url="${feign.accounts.url}", configuration = OAuthFeignConfiguration.class)
public interface IAccountFeignRepository {

    @PutMapping("accounts/updateAmount/{id}")
    Mono<Boolean> updateAmount(@PathVariable String id, @RequestBody AccountRequestDTO request);

    @GetMapping("accounts/alias/{alias}")
    Mono<Account> findAccountAlias(@PathVariable String alias) throws ResourceNotFound, EmptyRequiredField, ServerErrorException;

    @GetMapping("accounts/cvu/{cvu}")
    Mono<Account> findAccountCVU(@PathVariable String cvu) throws ResourceNotFound, EmptyRequiredField, ServerErrorException;

    @GetMapping("accounts/amount/{cvu}")
    Mono<Double> getAmountByCVU(@PathVariable String cvu) throws ReactiveFeignException;

    @PutMapping("admin/accounts/updateAmounts")
    Mono<Boolean> transactionalUpdate(@RequestBody UpdateAmountDTO request);

}
