package com.digitalAlpha.transactions.model.accounts;

import com.digitalAlpha.transactions.model.enums.CurrencyEnum;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class AccountCC extends AbstractAccount{
    private String alias;
    private String cvu;
    private Double availableAmount;
    private CurrencyEnum currency;

    @Override
    public String getTransactionId() {
        return cvu;
    }

    @Override
    public Double getAmount() {
        return availableAmount;
    }
}
