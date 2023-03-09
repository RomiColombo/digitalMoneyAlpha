package com.digitalAlpha.accounts.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum AccountTypesEnum {
    CA("CA"),
    CC("CC");

    private final String type;
}
