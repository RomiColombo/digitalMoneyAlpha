package com.digitalAlpha.middleware.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KafkaTransactionRequestDTO {
    private String userId;
    private String cvuFrom;
    private String cvuTo;
    private String userIdTo;
    private Double amount;
    private String description;
}
