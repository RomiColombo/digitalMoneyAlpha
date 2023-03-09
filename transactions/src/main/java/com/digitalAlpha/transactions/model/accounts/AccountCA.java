package com.digitalAlpha.transactions.model.accounts;

import com.digitalAlpha.transactions.model.enums.CurrencyEnum;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class AccountCA extends AbstractAccount{
    private String alias;
    private String cvu;
    private Double availableAmount;
    private CurrencyEnum currency;

    @Override
    public String getTransactionId() {
        return cvu;
    }

    @Override
    public String getAlias(){
        return alias;
    }

    @Override
    public Double getAmount() {
        return availableAmount;
    }
}
