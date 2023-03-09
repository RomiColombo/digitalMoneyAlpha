package com.digitalAlpha.middleware.model;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositDTO {

    private Double amount;
    private String userId;
    private Long cardNumber;
    private String accountId;

}
