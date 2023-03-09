package com.digitalAlpha.users.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccountDTO {

    private String firstname;
    private String lastname;
    private String dni;
    private String email;
    private String phone;
    private List<AccountDTO<?>> accounts;
    private List<TransactionDTO> transactions;

}