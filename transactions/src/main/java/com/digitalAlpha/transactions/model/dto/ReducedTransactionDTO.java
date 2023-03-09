package com.digitalAlpha.transactions.model.dto;

import com.digitalAlpha.transactions.model.enums.TransactionStatus;
import com.digitalAlpha.transactions.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReducedTransactionDTO {

    private String id;
    private TransactionType type;
    private String accountTo;
    private Double amount;
    private LocalDateTime time;
    private TransactionStatus status;
}
