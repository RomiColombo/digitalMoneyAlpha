package com.digitalAlpha.transactions.model.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositDTO {

    private String userId;
    private Double amount;
    private Long cardNumber;
    private String accountId;

}
