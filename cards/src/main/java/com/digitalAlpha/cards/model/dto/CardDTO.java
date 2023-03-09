package com.digitalAlpha.cards.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDTO {

    private String id;
    private String accountId;
    private Long cardNumber;
    private String expirationDate;
    private String fullName;
    private String companyName;
    private String userId;
}
