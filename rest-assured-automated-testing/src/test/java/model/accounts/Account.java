package model.accounts;

import model.accounts.enums.AccountTypesEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account <A>{

    private String id;
    private AccountTypesEnum type;
    private String userId;
    private boolean isValid;
    private A account;
}
