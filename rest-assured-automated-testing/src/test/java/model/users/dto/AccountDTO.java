package model.users.dto;

import lombok.*;
import model.accounts.enums.AccountTypesEnum;

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
