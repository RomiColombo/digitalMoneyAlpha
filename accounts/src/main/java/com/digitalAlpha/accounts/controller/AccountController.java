package com.digitalAlpha.accounts.controller;

import com.digitalAlpha.accounts.exception.*;
import com.digitalAlpha.accounts.model.Account;
import com.digitalAlpha.accounts.controller.cto.CreateAccount;
import com.digitalAlpha.accounts.model.AccountRequestDTO;
import com.digitalAlpha.accounts.service.IAccountService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.security.Principal;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class AccountController {

    private final IAccountService service;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PostAuthorize("returnObject.userId == authentication.name || hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Mono<Account> findAccountById(@PathVariable String id) throws ResourceNotFound, EmptyRequiredField {
        return service.getById(id);
    }

    @GetMapping("/amount/{cvu}")
    @ResponseStatus(HttpStatus.OK)
    @PostAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Mono<Double> getAmountByCVU(@PathVariable String cvu) throws ResourceNotFound, EmptyRequiredField {
        return service.getAmountByCVU(cvu);
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("authentication.name == #userId  || hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Flux<Account> findAccountsByUserId(@PathVariable("userId") String userId) throws ResourceNotFound {
        return service.getByUserId(userId);
    }

    @GetMapping("/alias/{alias}")
    @ResponseStatus(HttpStatus.OK)
    @PostAuthorize("returnObject.userId == authentication.name || hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Mono<Account> findAccountAlias(@PathVariable String alias) throws ResourceNotFound, EmptyRequiredField, ServerErrorException {
        return service.getByAlias(alias);
    }

    @GetMapping("/cvu/{cvu}")
    @ResponseStatus(HttpStatus.OK)
    @PostAuthorize("returnObject.userId == authentication.name || hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Mono<Account> findAccountCVU(@PathVariable String cvu) throws ResourceNotFound, EmptyRequiredField, ServerErrorException {
        return service.getByCVU(cvu);
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("authentication.name == #account.userId || hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Mono<Account<?>> createAccount(CreateAccount account) throws AlreadyExist, EmptyRequiredField, IOException {
        return service.save(account.getUserId(), account.getType());
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("authentication.name != null || hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Mono<Account> updateAlias(@PathVariable String id, @RequestBody String alias,@AuthenticationPrincipal Principal principal) throws AlreadyExist, EmptyRequiredField, ResourceNotFound, BadField, ServerErrorException, ExecutionException, InterruptedException {
        return service.updateAlias(id, alias, principal.getName());
    }

    @PutMapping("/updateAmount/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("authentication.name != null || hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Mono<Boolean> updateAmount(@PathVariable String id, @RequestBody AccountRequestDTO transaction) throws EmptyRequiredField, ResourceNotFound, BadField {
        return service.updateAmount(id, transaction);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("authentication.name != null || hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Mono<Void> invalidateAccount(@PathVariable String id,@AuthenticationPrincipal Principal principal) throws EmptyRequiredField, IOException, ResourceNotFound, ServerErrorException, ExecutionException, InterruptedException {
        return service.invalidate(id, principal);
    }
}