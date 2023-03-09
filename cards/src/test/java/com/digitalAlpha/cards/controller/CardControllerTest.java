package com.digitalAlpha.cards.controller;

import com.digitalAlpha.cards.model.Card;
import com.digitalAlpha.cards.model.dto.CardDTO;
import com.digitalAlpha.cards.service.impl.CardService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SuppressWarnings("all")
@WithMockUser(roles = "ADMIN",username = "123456")
@AutoConfigureMockMvc(addFilters = false)
@WebFluxTest(CardController.class)
public class CardControllerTest {

    private final String CARD_UUID = UUID.randomUUID().toString();
    private final String ACCOUNT_UUID = UUID.randomUUID().toString();
    private final Long CARD_NUMBER = Long.valueOf(123456789);
    private final String COMPANY_NAME_VISA = "Visa";
    private final String EXPIRATION_DATE = "12/23";
    private final String FULL_NAME = "Nombre Apellido";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CardService cardService;

    @Test
    public void FindCardsByAccountId_SuccessfullyTest() {
        // Given these instructions
        Mockito.when(cardService.findCardsByAccountId(any(String.class))).thenReturn(Flux.just(buildExpectedDTOResponse()));

        // When this happens
        webTestClient.get()
                .uri("/cards/getAll/{accountId}", ACCOUNT_UUID)
                .exchange()
                .expectStatus().isOk();

        // Then assert
        Mockito.verify(cardService, Mockito.times(1)).findCardsByAccountId(any(String.class));
        Mockito.verifyNoMoreInteractions(cardService);
    }

    @Test
    public void FindCardById_SuccessfullyTest() {
        // Given these instructions
        Mockito.when(cardService.findCardById(any(String.class))).thenReturn(Mono.just(buildExpectedDTOResponse()));

        // When this happens
        webTestClient.get()
                .uri("/cards/getCard/{cardId}", CARD_UUID)
                .exchange()
                .expectStatus().isOk();

        // Then assert
        Mockito.verify(cardService, Mockito.times(1)).findCardById(any(String.class));
        Mockito.verifyNoMoreInteractions(cardService);
    }

    @Test
    public void FindCardByCardNumber_SuccessfullyTest() {
        // Given these instructions
        Mockito.when(cardService.findCardByNumber(any(Long.class))).thenReturn(Mono.just(buildExpectedEntityResponse()));

        // When this happens
        webTestClient.get()
                .uri("/cards/getCard/cardNumber/{cardNumber}", CARD_NUMBER)
                .exchange()
                .expectStatus().isOk();

        // Then assert
        Mockito.verify(cardService, Mockito.times(1)).findCardByNumber(any(Long.class));
        Mockito.verifyNoMoreInteractions(cardService);
    }

    @Test
    public void SaveCard_SuccessfullyTest() {
        // Given these instructions
        when(cardService.saveCard(any(Card.class))).thenReturn(Mono.just(buildExpectedEntityResponse()));

        // When this happens
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).post()
                .uri("/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just(buildExpectedEntityResponse()), Card.class))
                .exchange()
                .expectStatus().isCreated();

        // Then assert
        Mockito.verify(cardService, Mockito.times(1)).saveCard(any(Card.class));
        Mockito.verifyNoMoreInteractions(cardService);
    }
/*
    @Test
    public void DeleteCardById_SuccessfullyTest() {
        // When this happens
        webTestClient.delete()
                .uri("/cards/{cardId}", CARD_UUID)
                .exchange()
                .expectStatus().isOk();

        // Then assert
        Mockito.verify(cardService, Mockito.times(1)).deleteCard(any(String.class));
        Mockito.verifyNoMoreInteractions(cardService);
    }
*/
    private Card buildExpectedEntityResponse() {
        return Card.builder()
            .id(CARD_UUID)
            .accountId(ACCOUNT_UUID)
            .cardNumber(CARD_NUMBER)
            .companyName(COMPANY_NAME_VISA)
            .expirationDate(EXPIRATION_DATE)
            .fullName(FULL_NAME)
            .build();
    }

    private CardDTO buildExpectedDTOResponse() {
        return CardDTO.builder()
                .id(CARD_UUID)
                .accountId(ACCOUNT_UUID)
                .cardNumber(CARD_NUMBER)
                .companyName(COMPANY_NAME_VISA)
                .expirationDate(EXPIRATION_DATE)
                .fullName(FULL_NAME)
                .build();
    }
}
