package com.digitalAlpha.transactions.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublishTransactionDTO {

    private String messageKey;
    private String action;
    private DepositDTO depositDTO;
    private TransactionRequestDTO kafkaTransactionRequestDTO;

}
