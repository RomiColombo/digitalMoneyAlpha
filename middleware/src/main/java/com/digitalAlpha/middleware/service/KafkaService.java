package com.digitalAlpha.middleware.service;

import com.digitalAlpha.middleware.configuration.MessageProducer;
import com.digitalAlpha.middleware.model.DepositDTO;
import com.digitalAlpha.middleware.model.KafkaTransactionRequestDTO;
import com.digitalAlpha.middleware.model.PublishTransactionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

    private final ObjectMapper objectMapper;
    private final MessageProducer messageProducer;

    private final String KAFKA_DEPOSIT_ACTION = "DEPOSIT";
    private final String KAFKA_TRANSACTIONS_TOPIC = "transactions";

    public void publishDeposit(String id, DepositDTO depositDTO) {
        messageProducer.publishMessage(KAFKA_TRANSACTIONS_TOPIC, id, objectToMessage(PublishTransactionDTO.builder()
                .messageKey(id)
                .action(KAFKA_DEPOSIT_ACTION)
                .depositDTO(depositDTO)
                .build()));
    }
    public void publishTransaction(String id, KafkaTransactionRequestDTO dto) {
        String KAFKA_TRANSFER_ACTION = "TRANSFER";
        messageProducer.publishMessage(KAFKA_TRANSACTIONS_TOPIC, id, objectToMessage(PublishTransactionDTO.builder()
                .messageKey(id)
                .action(KAFKA_TRANSFER_ACTION)
                .kafkaTransactionRequestDTO(dto)
                .build()));
    }

    // --- Internal usage methods ---

    private String objectToMessage(Object object) {
        String message;

        try {
            message = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("An error has occurred, can't transform data to message");
        }

        return message;
    }

}
