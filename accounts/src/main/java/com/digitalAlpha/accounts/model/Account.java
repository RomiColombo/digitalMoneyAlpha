package com.digitalAlpha.accounts.model;

import com.digitalAlpha.accounts.model.enums.AccountTypesEnum;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("accounts")
public class Account<A>{
    @Id
    private String id;
    private AccountTypesEnum type;
    private String userId;
    private boolean valid;
    private A account;

    public static boolean is(String test){
        return Arrays.stream(AccountTypesEnum.values()).map(Enum::toString).toList().contains(test);
    }
}