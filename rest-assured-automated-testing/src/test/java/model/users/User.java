package model.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String email;
    private String firstname;
    private String lastname;
    private String dni;
    private String phone;
    private String password;

}
