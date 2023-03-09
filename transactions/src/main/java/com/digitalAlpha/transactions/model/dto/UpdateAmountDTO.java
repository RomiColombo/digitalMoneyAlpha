package com.digitalAlpha.transactions.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateAmountDTO {
    private String accountFrom;
    private Double accountFromAmount;
    private String accountTo;
    private Double accountToAmount;
}
