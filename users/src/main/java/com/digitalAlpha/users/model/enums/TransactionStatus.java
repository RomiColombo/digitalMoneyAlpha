package com.digitalAlpha.users.model.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TransactionStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    FAIL("FAIL");

    private String status;

}
