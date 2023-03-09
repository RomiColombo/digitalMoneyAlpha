package com.digitalAlpha.accounts.model;

import com.digitalAlpha.accounts.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccountRequestDTO {

    private Double amount;
    private TransactionType type;
}
