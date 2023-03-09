package com.digitalAlpha.accounts.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateAmountDTO {
    private String accountFrom;
    private Double accountFromAmount;
    private String accountTo;
    private Double accountToAmount;
}
