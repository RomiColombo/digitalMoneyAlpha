package com.digitalAlpha.cards.service.impl;


import com.digitalAlpha.cards.model.Card;
import com.digitalAlpha.cards.repository.IAccountsFeignRepository;
import com.digitalAlpha.cards.repository.ICardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CardsServiceTest {

    private static final String ACCOUNT_ID = UUID.randomUUID().toString();
    private static final Integer CODE = 123;
    private static final Long CARD_NUMBER = 450214756987321L;
    private static final String EXPIRATION_DATE = "12/25";
    private static final String FULL_NAME = "Nombre Apellido";


    @Mock
    private ICardRepository cardRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private IAccountsFeignRepository accountsFeignRepository;

    @InjectMocks
    private CardService cardService;

    @Test
    public void CreateCards_SuccesfullyTest() throws Exception{

        //Give these instructions
        Card expectedServiceResponse = buildCard();

    }


    private Card buildCard(){
        return Card.builder()
                .accountId(ACCOUNT_ID)
                .code(CODE)
                .cardNumber(CARD_NUMBER)
                .expirationDate(EXPIRATION_DATE)
                .fullName(FULL_NAME)
                .build();
    }
}
