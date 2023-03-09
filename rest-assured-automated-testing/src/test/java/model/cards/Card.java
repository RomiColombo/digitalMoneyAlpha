package model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Card {

    private String id;
    private String accountId;
    private Integer code;
    private Long cardNumber;
    private String expirationDate;
    private String fullName;
    private String companyName;
    private String userId;

}
