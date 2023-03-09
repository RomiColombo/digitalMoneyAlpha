package com.digitalAlpha.users.model.dto;

import com.digitalAlpha.users.model.enums.TransactionStatus;
import com.digitalAlpha.users.model.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDTO {

    private String id;
    private TransactionType type;
    private String accountTo;
    private Double amount;
    private LocalDateTime time;
    private TransactionStatus status;

}
