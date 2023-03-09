package com.digitalAlpha.cards.repository;

import com.digitalAlpha.cards.exception.BadFieldException;
import com.digitalAlpha.cards.exception.ServerErrorException;
import com.digitalAlpha.cards.model.Card;
import com.digitalAlpha.cards.model.dto.CardDTO;
import com.digitalAlpha.cards.exception.ResourceNotFound;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICardRepository {

    Flux<CardDTO> findCardsByAccountId(String accountId) throws ResourceNotFound;
    Mono<Card> findCardById(String id) throws ResourceNotFound;
    Mono<Card> saveCard(Card card) throws ServerErrorException;
    Mono<Boolean> DeleteCard(String id) throws BadFieldException, ResourceNotFound;

    Mono<Card> findCardByCardNumber(Long cardNumber);
}
