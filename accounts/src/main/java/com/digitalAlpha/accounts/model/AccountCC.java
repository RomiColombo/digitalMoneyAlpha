package com.digitalAlpha.accounts.model;

import com.digitalAlpha.accounts.model.enums.Currency;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class AccountCC {
    private String alias;
    private String cvu;
    private Double availableAmount;
    private Currency currency;
}
