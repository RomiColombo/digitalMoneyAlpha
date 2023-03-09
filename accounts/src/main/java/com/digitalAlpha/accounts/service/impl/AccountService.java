package com.digitalAlpha.accounts.service.impl;

import com.digitalAlpha.accounts.exception.*;
import com.digitalAlpha.accounts.model.Account;
import com.digitalAlpha.accounts.model.AccountCA;
import com.digitalAlpha.accounts.model.AccountCC;
import com.digitalAlpha.accounts.model.AccountRequestDTO;
import com.digitalAlpha.accounts.model.enums.AccountTypesEnum;
import com.digitalAlpha.accounts.model.enums.Currency;
import com.digitalAlpha.accounts.repository.IAccountRepository;
import com.digitalAlpha.accounts.service.IAccountService;
import com.digitalAlpha.accounts.utils.AccountMaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.io.IOException;
import java.security.Principal;
import java.util.function.Consumer;

import static com.digitalAlpha.accounts.model.Account.is;


@Service
@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
public class
AccountService implements IAccountService {
    IAccountRepository repository;
    @Autowired
    public AccountService(IAccountRepository repository) {
        this.repository = repository;
    }


    @Override
    public Mono<Account<?>> save(String userId, String type) throws AlreadyExist, EmptyRequiredField, IOException {
        if (!is(type.toUpperCase())) throw new BadField("the type of account is not  available");
        Account<?> account = Account.builder()
                .userId(userId)
                .type(AccountTypesEnum.valueOf(type.toUpperCase()))
                .valid(true)
                .build();
        if (account.getType() == AccountTypesEnum.CA) {

            Account<AccountCA> newAccount = (Account<AccountCA>) account;
            AccountCA accountCA = AccountCA.builder()
                    .alias(AccountMaker.getRandomAlias())
                    .availableAmount(0.0)
                    .cvu(AccountMaker.getRandomCvu())
                    .currency(Currency.ARS)
                    .build();
            newAccount.setAccount(accountCA);
            return repository.findAccountsByUserId(userId).map(acc ->{
                if (acc.getType().equals(AccountTypesEnum.CA)) throw new AlreadyExist("account of type ca");
                return acc;
            }).then(repository.save(newAccount));
        }
        if (account.getType() == AccountTypesEnum.CC) {
            Account<AccountCC> newAccount = (Account<AccountCC>) account;
            AccountCC accountCC = AccountCC.builder()
                    .alias(AccountMaker.getRandomAlias())
                    .availableAmount(0.0)
                    .cvu(AccountMaker.getRandomCvu())
                    .currency(Currency.ARS)
                    .build();
            newAccount.setAccount(accountCC);
            return repository.findAccountsByUserId(userId).map(acc ->{
                if (acc.getType().equals(AccountTypesEnum.CC)) throw new AlreadyExist("account of type cc");
                return acc;
            }).then(repository.save(newAccount));
        }
        throw new ServerErrorException("something happened saving account");
    }

    @Override
    public Flux<Account> getByUserId(String userId) throws ResourceNotFound {
        if (userId == null) throw new ResourceNotFound("Can't find username, field is null");
        return repository.findAccountsByUserId(userId);
    }

    @Override
    public Mono<Account> getById(String id) throws ResourceNotFound, EmptyRequiredField {
        if (id == null || id.equals("")) throw new EmptyRequiredField("Can't find account, id field is required");
        return repository.findById(id);
    }

    @Override
    public Mono<Double> getAmountByCVU(String cvu) throws ResourceNotFound, EmptyRequiredField {
        if (cvu == null || cvu.equals("")) throw new EmptyRequiredField("Can't find amount, cvu field is required");
        return repository.getAmountByCVU(cvu);
    }

    @Override
    public Mono<Void> invalidate(String id, Principal principal) throws EmptyRequiredField, ServerErrorException, ResourceNotFound {
        if (id == null || id.equals("")) throw new EmptyRequiredField("Can't find account, id field is required");
        Mono <Account> res = repository.findById(id).zipWith(hasAuthority()).doOnError(onError()).doOnSuccess( t -> {
            if (!t.getT2()){
                if(!t.getT1().getUserId().equals(principal.getName())) throw new AccessDeniedException("Access denied");
            }
        }).map(Tuple2::getT1);

        res.doOnNext(acc -> {
            if (acc.getType() == AccountTypesEnum.CA) {
                Double amount = ((Account<AccountCA>) acc).getAccount().getAvailableAmount();
                if (amount == 0.0) throw new ServerErrorException("contact with support, can't invalidate accounts");
            }
        });
        return res.then(repository.invalidateById(id));
    }

    @Override
    public Mono<Account> updateAlias(String id, String alias, String userId) throws AlreadyExist, ResourceNotFound, BadField, EmptyRequiredField, ServerErrorException {
        if (id == null || id.equals("")) throw new EmptyRequiredField("Can't find account, id field is required");
        repository.findById(id).map(Account::getUserId).zipWith(hasAuthority()).doOnNext( t ->  {
            if (!t.getT1().equals(userId) || !t.getT2()) throw new AccessDeniedException("access denied");
        });
        return repository.updateAlias(id, alias);
    }

    @Override
    public Mono<Boolean> updateAmount(String id, AccountRequestDTO transaction) throws BadField, ResourceNotFound, EmptyRequiredField {
        if (id == null || id.equals(""))
            throw new EmptyRequiredField("Can't find account, id field is required");
        return repository.updateAmount(id, transaction.getAmount(), transaction.getType());
    }

    @Override
    public Mono<Account> getByAlias(String alias) throws ResourceNotFound, ServerErrorException, EmptyRequiredField {
        if (alias == null || alias.equals(""))
            throw new EmptyRequiredField("Can't find account, alias field is required");
        return repository.findByAlias(alias);
    }

    @Override
    public Mono<Account> getByCVU(String cvu) throws ResourceNotFound, ServerErrorException, EmptyRequiredField {
        if (cvu == null || cvu.equals(""))
            throw new EmptyRequiredField("Can't find account, cvu field is required");
        return repository.findByCVU(cvu);
    }

    @Override
    public Mono<Boolean> validateAccountByUserId(String accountId, String userId) throws ResourceNotFound {
        Flux<Account> res = repository.findAccountsByUserId(userId);
        return res.filter(acc -> acc.getId().equals(accountId)).collectList().map( list -> {
            if (list.isEmpty()) throw new ResourceNotFound("Error: The account does not belong to the user");
            return true;
        });
    }

    private Mono<Boolean> hasAuthority() {
        return ReactiveSecurityContextHolder.getContext()
                .map(a ->a.getAuthentication().getAuthorities().stream().anyMatch(auth ->
                        auth.getAuthority().equals("ROLE_ADMIN")
                                || auth.getAuthority().equals("ROLE_SERVICE")));
    }

    private Consumer<Throwable> onError(){
        return e -> {
            throw new ServerErrorException("contact with support of digital Alpha");
        };
    }
}