package com.digitalAlpha.transactions.model;

import com.digitalAlpha.transactions.model.enums.TransactionStatus;
import com.digitalAlpha.transactions.model.enums.TransactionType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("transactions")
public class Transaction {
    @Id
    private String id;
    private TransactionType type;
    private String userId;
    private LocalDateTime time;
    private TransactionStatus status;
    private String accountFrom;
    private String accountTo;
    private Double amount;
    private String description;
    private Double fee;


}
