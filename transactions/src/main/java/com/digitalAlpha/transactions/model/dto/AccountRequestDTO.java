package com.digitalAlpha.transactions.model.dto;

import com.digitalAlpha.transactions.model.enums.TransactionType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountRequestDTO {
    private Double amount;
    private TransactionType type;
}
