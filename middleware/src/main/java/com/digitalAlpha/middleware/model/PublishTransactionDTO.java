package com.digitalAlpha.middleware.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublishTransactionDTO {

    private String messageKey;
    private String action;
    private DepositDTO depositDTO;
    private KafkaTransactionRequestDTO kafkaTransactionRequestDTO;

}
