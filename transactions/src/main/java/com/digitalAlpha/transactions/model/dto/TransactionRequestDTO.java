package com.digitalAlpha.transactions.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionRequestDTO {
    private String userId;
    private String cvuFrom;
    private String cvuTo;
    private String userIdTo;
    private Double amount;
    private String description;
}
