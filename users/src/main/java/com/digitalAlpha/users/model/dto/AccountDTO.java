package com.digitalAlpha.users.model.dto;

import com.digitalAlpha.users.model.enums.AccountTypesEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDTO<A> {

    private String id;
    private AccountTypesEnum type;
    private String userId;
    private boolean isValid;
    private A account;


}
