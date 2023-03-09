package com.digitalAlpha.accounts.controller;

import com.digitalAlpha.accounts.exception.ResourceNotFound;
import com.digitalAlpha.accounts.model.UpdateAmountDTO;
import com.digitalAlpha.accounts.service.IAccountService;
import com.digitalAlpha.accounts.service.impl.TransactionalAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IAccountService service;

    private final TransactionalAccountService transactionalService;

    @GetMapping("/account/{accountId}/validate/{userId}")
    @PreAuthorize("hasAuthority('ROLE_SERVICE')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Boolean> validateAccountByUserId(@PathVariable String accountId, @PathVariable String userId) throws ResourceNotFound {
        return service.validateAccountByUserId(accountId, userId);
    }

    @PutMapping("/accounts/updateAmounts")
    @PreAuthorize("hasAuthority('ROLE_SERVICE')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Boolean> updateAmount(@RequestBody UpdateAmountDTO updateAmount) throws ResourceNotFound {
        return transactionalService.updateAmount(updateAmount);
    }
}
