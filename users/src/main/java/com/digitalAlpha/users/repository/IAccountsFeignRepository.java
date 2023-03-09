package com.digitalAlpha.users.repository;


import com.digitalAlpha.users.configuration.feign.OAuthFeignConfiguration;
import com.digitalAlpha.users.model.dto.AccountDTO;
import feign.Headers;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "ms-accounts", url="${feign.accounts.url}", configuration = OAuthFeignConfiguration.class)
public interface IAccountsFeignRepository {

    @GetMapping("/accounts/user/{userId}")
    Flux<AccountDTO<?>> findAccountsByUserId(@PathVariable("userId") String id);

    @PostMapping(path = "/accounts",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Mono<AccountDTO<?>> save(@RequestBody MultiValueMap<String,String> map);

    @DeleteMapping("/accounts/{id}")
    Mono<String> invalidateAccount(@PathVariable("id") String id);


}