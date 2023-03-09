package com.digitalAlpha.users.model.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterDTO {

    private String email;
    private String firstname;
    private String lastname;
    private String dni;
    private String phone;
    private String password;
}