package com.digitalAlpha.transactions.model.accounts;

public abstract class AbstractAccount implements Transferable {

    public abstract String getAlias();

    public abstract Double getAmount();
}
