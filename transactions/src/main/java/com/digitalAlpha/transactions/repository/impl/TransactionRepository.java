package com.digitalAlpha.transactions.repository.impl;

import com.digitalAlpha.transactions.exception.BadField;
import com.digitalAlpha.transactions.exception.ResourceNotFound;
import com.digitalAlpha.transactions.model.Transaction;
import com.digitalAlpha.transactions.model.dto.ReducedTransactionDTO;
import com.digitalAlpha.transactions.model.enums.TransactionStatus;
import com.digitalAlpha.transactions.repository.ITransactionRepository;
import com.digitalAlpha.transactions.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class TransactionRepository implements ITransactionRepository {


    private final ReactiveMongoTemplate template;


    private final String COLLECTION = "transactions";

    @Override
    public Mono<Transaction> createTransaction(Transaction transaction) {
        return template.insert(transaction,COLLECTION);
    }

    @Override
    public Mono<Transaction> getById(String id) throws ResourceNotFound {
        Query query = new Query(Criteria.where("id").is(id));
        return template.findOne(query, Transaction.class).doOnNext(t -> {
            if (t == null) throw new ResourceNotFound("id " + id + " is not a transaction");
        });
    }

    @Override
    public Flux<ReducedTransactionDTO> getByFilters(Map<String, Object> filters, String userId) throws BadField {
        System.out.println("repo");
        Query query = new Query((Criteria.where("userId").is(userId)));

        if (filters.get("limit") != null) {
            query.limit((Integer) filters.get("limit"));
        }

        if (filters.get("amount") != null) {
            List<Double> range = (List<Double>) filters.get("amount");
            query.addCriteria(Criteria.where("amount").gte(range.get(0)).lte(range.get(1)));
        }
        if(filters.get("time") != null) {
            List<LocalDateTime> range = (List<LocalDateTime>) filters.get("time");
            query.addCriteria(Criteria.where("time").gte(range.get(0)).lte(range.get(1)));
        }
        if (filters.get("type") != null) {
            query.addCriteria(Criteria.where("type").is(filters.get("type")));
        }
        if (filters.get("status") != null) {
            query.addCriteria(Criteria.where("status").is(filters.get("status")));
        }

        if (filters.get("page") != null && filters.get("limit") != null) {

            Pageable pageable = PageRequest.of((Integer) filters.get("page"),(Integer) filters.get("limit"));
            query.with(pageable);
        }

        if (filters.get("cvu") != null) {
            query.addCriteria(Criteria.where("cvu").is(filters.get("cvu")));
        }

        if (filters.get("sort") != null) {
            List<String> sort = (List<String>) filters.get("sort");
            query.with(Sort.by(Sort.Direction.valueOf(sort.get(1)),sort.get(0)));
        }
        return template.find(query, Transaction.class).map(a -> {
            System.out.println(a.getId());
            return AppUtils.entityToReducedDto(a);
        });


    }

    @Override
    public Mono<Transaction> updateStatus(String id, TransactionStatus transactionStatus) throws ResourceNotFound {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update();
        update.set("status", transactionStatus);

        return template.updateFirst(query,update,Transaction.class, COLLECTION).flatMap(res -> getById(id));
    }

    @Override
    public Mono<TransactionStatus> getStatus(String id) {
        return template.findById(id, Transaction.class).map(Transaction::getStatus);
    }
}
