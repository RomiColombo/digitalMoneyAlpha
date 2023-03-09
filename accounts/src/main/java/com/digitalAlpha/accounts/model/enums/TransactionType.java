package com.digitalAlpha.accounts.model.enums;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TransactionType {

    IN("IN"),
    OUT("OUT"),
    DEPOSIT("DEPOSIT");

    private final String type;
}
