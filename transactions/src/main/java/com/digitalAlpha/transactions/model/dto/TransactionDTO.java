package com.digitalAlpha.transactions.model.dto;

import com.digitalAlpha.transactions.model.enums.TransactionType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private TransactionType type;
    private String userId;
    private String accountFrom;
    private String accountTo;
    private Double amount;
    private String description;
}
