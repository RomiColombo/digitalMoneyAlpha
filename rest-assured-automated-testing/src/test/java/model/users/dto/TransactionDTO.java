package model.users.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class TransactionDTO {

    private String id;
    private String accountFrom;
    private Double amount;
    private String description;
    private String accountTo;
    private String type;

}
