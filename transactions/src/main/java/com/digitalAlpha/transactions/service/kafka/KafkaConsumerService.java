package com.digitalAlpha.transactions.service.kafka;

import com.digitalAlpha.transactions.exception.InvalidTransaction;
import com.digitalAlpha.transactions.model.dto.PublishTransactionDTO;
import com.digitalAlpha.transactions.service.ITransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final ITransactionService transactionService;


    @KafkaListener(topics = "transactions", groupId = "1")
    public void listenerDeposit(String message) throws InterruptedException, InvalidTransaction {
        log.info("Message recieved: " + message);

        PublishTransactionDTO p = messageToEntity(message);
        String action = p.getAction();

        switch (action) {
            case "DEPOSIT":
                transactionService.createDeposit(p).subscribe();
                break;
            case "TRANSFER":
                transactionService.createTransaction(p)
                        .onErrorStop().subscribe();
                break;
        }
    }

    private PublishTransactionDTO messageToEntity(String message) {
        PublishTransactionDTO p;
        try {
            p = objectMapper.readValue(message, PublishTransactionDTO.class);
        } catch(JsonProcessingException ex) {
            log.error(ex.getMessage());
            throw new RuntimeJsonMappingException("Couldn't transform message to Object");
        }
        return p;
    }

}
