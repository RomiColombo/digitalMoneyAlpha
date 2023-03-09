package com.digitalAlpha.accounts.repository.impl;

import com.digitalAlpha.accounts.exception.ServerErrorException;
import com.digitalAlpha.accounts.model.Account;
import com.digitalAlpha.accounts.model.UpdateAmountDTO;
import com.digitalAlpha.accounts.repository.ITransactionalAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TransactionalAccountRepository implements ITransactionalAccountRepository {
    private final ReactiveMongoTemplate template;

    @Override
    @Transactional
    public Mono<Boolean> updateAmount(UpdateAmountDTO updateAmountDTO) {
        log.info("[TransactionalAccountRepository]: start updateAmount");
        Query outAmountQuery = new Query(Criteria.where("account.cvu").is(updateAmountDTO.getAccountFrom()))
                    .addCriteria(Criteria.where("valid").is(true));
            Update outAmountUpdate = new Update();
            outAmountUpdate.set("account.availableAmount",updateAmountDTO.getAccountFromAmount());
            Query inAmountQuery = new Query(Criteria.where("account.cvu").is(updateAmountDTO.getAccountTo()))
                    .addCriteria(Criteria.where("valid").is(true));
            Update inAmountUpdate = new Update();
            inAmountUpdate.set("account.availableAmount", updateAmountDTO.getAccountToAmount());
        return template.inTransaction()
                .execute(action -> action.updateFirst(outAmountQuery,outAmountUpdate,Account.class)
                        .doOnNext(up -> {
                            if (up.getModifiedCount() < 1) {
                                log.error("There is not an update of destination account");
                                throw new ServerErrorException("Can't update origin account");
                            }
                        })
                    .then(action.updateFirst(inAmountQuery,inAmountUpdate,Account.class)).doOnNext(up -> {
                        if (up.getModifiedCount() < 1) {
                            log.error("There is not an update of destination account");
                            throw new ServerErrorException("Can't update destination account");
                        }
                })).hasElements();
    }
}
