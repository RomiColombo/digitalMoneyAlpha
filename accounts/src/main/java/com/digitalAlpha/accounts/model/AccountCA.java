package com.digitalAlpha.accounts.model;

import com.digitalAlpha.accounts.model.enums.Currency;
import lombok.*;


@Getter
@Builder
public class AccountCA {
    private String alias;
    private String cvu;
    private Double availableAmount;
    private Currency currency;
}
