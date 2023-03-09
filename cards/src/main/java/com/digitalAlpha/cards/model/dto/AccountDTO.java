package com.digitalAlpha.cards.model.dto;

import com.digitalAlpha.cards.model.enums.AccountTypesEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO<A> {

    private String id;
    private AccountTypesEnum type;
    private String userId;
    private boolean isValid;
    private A account;

}
