package com.digitalAlpha.middleware.model;

import com.digitalAlpha.middleware.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequestDTO {
    private TransactionType type;
    private String userId;
    private String accountFrom;
    private Map<String, String> accountTo;
    private Double amount;
    private String description;
}
