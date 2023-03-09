package com.digitalAlpha.middleware.service.utils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CustomMessages {
    INVALID_AMOUNT("","Amount was %s but needs to be %s"),
    INVALID_ACCOUNT_TO("", "Either alias or cvu are required");

    private final String parameterName;
    private final String message;

    public String getMessage() {
        return message;
    }

    public String getMessageWithParams(String... params) {
        return String.format(message, (Object[]) params);
    }
}
