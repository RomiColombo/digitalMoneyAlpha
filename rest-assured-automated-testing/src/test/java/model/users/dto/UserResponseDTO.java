package model.users.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponseDTO {
    private String firstname;
    private String lastname;
    private String dni;
    private String email;
    private String phone;
    private List<AccountDTO<?>> accounts;
    private List<TransactionDTO> transactions;
}
