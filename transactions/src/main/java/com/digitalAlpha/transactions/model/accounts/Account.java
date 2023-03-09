package com.digitalAlpha.transactions.model.accounts;

import com.digitalAlpha.transactions.model.enums.AccountTypesEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Document("accounts")
public class Account<A extends AbstractAccount> implements Transferable {
    @Id
    private String id;
    private AccountTypesEnum type;
    private String userId;
    private boolean valid;
    private A account;

    @Override
    public String getTransactionId() {
        return account.getTransactionId();
    }

    @Override
    public String getAlias() {
        return account.getAlias();
    }

    @Override
    public Double getAmount() {
        return account.getAmount();
    }


}