package com.digitalAlpha.users.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserKeycloak {

    private String id;
    private String username;
    private String password;
    private String email;
    private String firstname;
    private String lastname;
    private String dni;
    private String phone;


}
