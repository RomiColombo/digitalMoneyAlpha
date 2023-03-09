package com.digitalAlpha.middleware.model.accounts;

import com.digitalAlpha.middleware.model.enums.AccountTypesEnum;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO<A extends AbstractAccount> implements Transferable {

    private String id;
    private AccountTypesEnum type;
    private String userId;
    private boolean valid;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = AccountCA.class, name = "CA"),
            @JsonSubTypes.Type(value = AccountCC.class, name = "CC")
    })
    private A account;

    @Override
    public String getUniqueIdentifier() {
        return account.getUniqueIdentifier();
    }

    @Override
    public String getAlias() {
        return account.getAlias();
    }

    @Override
    public Double getAmount(){
        return account.getAmount();
    }


}