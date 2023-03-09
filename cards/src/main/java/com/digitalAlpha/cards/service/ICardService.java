package com.digitalAlpha.cards.service;

import com.digitalAlpha.cards.exception.*;
import com.digitalAlpha.cards.model.Card;
import com.digitalAlpha.cards.model.dto.CardDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

public interface ICardService {

    Flux<CardDTO> findCardsByAccountId(String accountId) throws ResourceNotFound;
    Mono<CardDTO> findCardById(String cardId) throws ResourceNotFound;
    Mono<Card> saveCard(Card card) throws EmptyRequiredField, BadFieldException, AlreadyExist, ServerErrorException;
    // Card updateCard(String cardId, Card card);
    Mono<Card> findCardByNumber(Long cardNumber) throws ResourceNotFound;
    Mono<Void> deleteCard(String cardId, Principal principal) throws ResourceNotFound, BadFieldException;
}
