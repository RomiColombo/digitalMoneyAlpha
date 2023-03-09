package model.transactions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.transactions.enums.TransactionStatus;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
    private String id;
    private String type;
    private String user;
    private Date time;
    private TransactionStatus status;
    private String accountFrom;
    private String accountTo;
    private Double amount;
    private String description;
    private Double fee;
}

