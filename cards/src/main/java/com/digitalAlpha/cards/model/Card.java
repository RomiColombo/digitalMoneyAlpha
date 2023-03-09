package com.digitalAlpha.cards.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("cards")
public class Card {

    @Id
    private String id;
    private String accountId;
    private Integer code;
    private Long cardNumber;
    private String expirationDate;
    private String fullName;
    private String companyName;
    private String userId;

}
