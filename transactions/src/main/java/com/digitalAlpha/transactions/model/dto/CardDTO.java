package com.digitalAlpha.transactions.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDTO {

    private String id;
    private String accountId;
    private Integer code;
    private Long cardNumber;
    private String expirationDate;
    private String fullName;
    private String companyName;
    private String userId;

}