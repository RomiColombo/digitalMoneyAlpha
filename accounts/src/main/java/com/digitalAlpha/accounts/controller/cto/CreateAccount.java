package com.digitalAlpha.accounts.controller.cto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateAccount {
    private String userId;
    private String type;
}
