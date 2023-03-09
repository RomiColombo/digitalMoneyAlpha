package com.digitalAlpha.accounts.repository.impl;

import com.digitalAlpha.accounts.exception.*;
import com.digitalAlpha.accounts.model.Account;
import com.digitalAlpha.accounts.model.AccountCA;
import com.digitalAlpha.accounts.model.AccountCC;
import com.digitalAlpha.accounts.model.enums.AccountTypesEnum;
import com.digitalAlpha.accounts.model.enums.TransactionType;
import com.digitalAlpha.accounts.repository.IAccountRepository;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.function.Consumer;

@Repository
@Slf4j
@SuppressWarnings({"rawtypes","unchecked"})
@RequiredArgsConstructor
public class AccountRepository implements IAccountRepository {

    private final ReactiveMongoTemplate template;

    private final String COLLECTION = "accounts";

    @Override
    public Flux<Account> findAccountsByUserId(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId)).addCriteria(Criteria.where("valid").is(true));
        return template.find(query, Account.class, COLLECTION).doOnError(onError());
    }

    @Override
    public Mono<Account> findById(String id) throws ResourceNotFound {
        Query query = new Query(Criteria.where("id").is(id)).addCriteria(Criteria.where("valid").is(true));
        return template.findOne(query, Account.class, COLLECTION).doOnError(onError())
                .doOnSuccess(account1 -> {
                    if (account1 == null) throw new ResourceNotFound("id " + id + " doesn't exist");
                });
    }

    @Override
    public Mono<Double> getAmountByCVU(String cvu) throws ResourceNotFound{
        Query query = new Query(Criteria.where("account.cvu").is(cvu))
                .addCriteria(Criteria.where("valid").is(true));
        query.fields().include("account.availableAmount");
        return template.findOne(query, Account.class, COLLECTION)
                .doOnError(onError()).doOnSuccess(acc -> {
                    if (acc == null) throw new ResourceNotFound("Error: cvu " + cvu + " doesn't exist");
                })
                .map(acc -> (Double) ((LinkedHashMap)acc.getAccount()).get("availableAmount"));

    }

    @Override
    public Mono<Account<?>> save(Account account) {
        return template.insert(account, COLLECTION).map(acc -> (Account<?>) acc);
    }

    @Override
    @Async
    public Mono<Void> invalidateById(String id) throws ServerErrorException {
        Query query = new Query(Criteria.where("id").is(id))
                .addCriteria(Criteria.where("valid").is(true));
        query.fields().include("valid");
        Update update = new Update();
        update.set("valid", false);
        return template.updateFirst(query, update, Account.class, COLLECTION)
                .doOnError(onError()).then();
    }

    @Override
    public Mono<Account> updateAlias(String id, String alias) throws BadField {

        Query query = new Query(Criteria.where("id").is(id))
                .addCriteria(Criteria.where("valid").is(true));

        Update update = new Update();
        update.set("account.alias", alias);
        Mono<UpdateResult> result = template.updateFirst(query,update, Account.class, COLLECTION).doOnError(onError())
                .doOnSuccess(res -> {
                    if (res.getModifiedCount() == 0) throw new BadField("can't update account with id " + id);
                });
        return result.then(findById(id));
    }

    @Override
    public Mono<Boolean> updateAmount(String id, Double amount, TransactionType type) throws BadField {

            Query query0 = new Query(Criteria.where("id").is(id))
                    .addCriteria(Criteria.where("valid").is(true));

            Mono<Account> account = template.findOne(query0, Account.class, COLLECTION).doOnError(e ->{
                        throw new ServerErrorException("contact with support of digital Alpha");
                    })
                    .doOnNext(a-> {
                if(a == null) throw new ResourceNotFound("can't find account with id "+ id);
            });
            Mono<Double> lastAmount = account.map(a -> {
                if (a.getType() == AccountTypesEnum.CA) return (Account<AccountCA>) a;
                if (a.getType() == AccountTypesEnum.CC) return (Account<AccountCC>) a;
                return a;
            }).map(acc -> ((Account<AccountCA>) acc).getAccount().getAvailableAmount());
            Mono<UpdateResult> result = lastAmount.flatMap(last -> {
                Query query = new Query(Criteria.where("id").is(id));
                Update update = new Update();
                update.set("account.availableAmount", last + amount);
                return template.updateFirst(query,update,Account.class);
            }).doOnNext(a -> {
                if(a.getModifiedCount() == 0 ) throw new ServerErrorException("contact with support of digital Alpha");
            });
            return result.then(Mono.just(true));

    }

    @Override
    public Mono<Account> findByAlias(String alias) throws ResourceNotFound {
        Query query = new Query(Criteria.where("account.alias").is(alias))
                .addCriteria(Criteria.where("valid").is(true));

        return template.findOne(query, Account.class).doOnError(onError()).doOnSuccess(acc -> {
            if (acc == null) throw new ResourceNotFound("Error: alias " + alias + " doesn't exists");
        });
    }

    @Override
    public Mono<Account> findByCVU(String cvu) throws ResourceNotFound, ServerErrorException{
        Query query = new Query(Criteria.where("account.cvu").is(cvu))
                .addCriteria(Criteria.where("valid").is(true));

        return template.findOne(query, Account.class).doOnError(onError()).doOnSuccess(acc -> {
            if (acc == null) throw new ResourceNotFound("Error: cvu " + cvu + " doesn't exists");
        });
    }

    private Consumer<Throwable> onError(){
        return e -> {
            throw new ServerErrorException("contact with support of digital Alpha");
        };
    }
}