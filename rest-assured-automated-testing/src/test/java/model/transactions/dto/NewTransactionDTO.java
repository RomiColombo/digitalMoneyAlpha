package model.transactions.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewTransactionDTO {

    private String type;
    private String user;
    private String accountFrom;
    private String accountTo;
    private Double amount;
    private String description;

}