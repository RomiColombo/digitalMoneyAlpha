package com.digitalAlpha.middleware.model;

import com.digitalAlpha.middleware.model.enums.TransactionStatus;
import com.digitalAlpha.middleware.model.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDTO {
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
