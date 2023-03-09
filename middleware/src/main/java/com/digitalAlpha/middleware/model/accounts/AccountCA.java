package com.digitalAlpha.middleware.model.accounts;

import com.digitalAlpha.middleware.model.enums.CurrencyEnum;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCA extends AbstractAccount{
    private String alias;
    private String cvu;
    private Double availableAmount;
    private CurrencyEnum currency;

    @Override
    public String getUniqueIdentifier() {
        return cvu;
    }

    @Override
    public String getAlias(){
        return alias;
    }

    @Override
    public Double getAmount(){
        return availableAmount;
    }
}
