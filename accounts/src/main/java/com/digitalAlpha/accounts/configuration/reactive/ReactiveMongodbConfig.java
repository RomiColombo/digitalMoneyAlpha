package com.digitalAlpha.accounts.configuration.reactive;

import com.mongodb.reactivestreams.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
public class ReactiveMongodbConfig {

    @Autowired
    MongoClient mongoClient;

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(mongoClient, "accounts");
    }

    @Bean
    public ReactiveMongoTransactionManager transactionManager(ReactiveMongoDatabaseFactory factory){
        return new ReactiveMongoTransactionManager(factory);
    }

    @Bean
    public TransactionalOperator transactionalOperator(ReactiveTransactionManager manager){
        return TransactionalOperator.create(manager);
    }
}
