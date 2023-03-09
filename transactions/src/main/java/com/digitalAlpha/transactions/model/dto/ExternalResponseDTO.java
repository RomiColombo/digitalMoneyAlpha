package com.digitalAlpha.transactions.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalResponseDTO {

    private Double amount;
    private Long cardNumber;
    private String currency;

}
