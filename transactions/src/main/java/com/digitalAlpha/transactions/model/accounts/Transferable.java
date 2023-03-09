package com.digitalAlpha.transactions.model.accounts;

public interface Transferable {

    String getTransactionId();

    String getAlias();

    Double getAmount();
}
