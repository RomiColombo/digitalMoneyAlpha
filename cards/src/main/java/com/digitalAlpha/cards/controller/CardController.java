package com.digitalAlpha.cards.controller;

import com.digitalAlpha.cards.exception.*;
import com.digitalAlpha.cards.model.Card;
import com.digitalAlpha.cards.model.dto.CardDTO;
import com.digitalAlpha.cards.service.ICardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;


@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class CardController {
    private final ICardService cardService;

    @GetMapping("/getAll/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    @PostAuthorize("authentication.name == returnObject.userId || hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Flux<CardDTO> findCardsByAccountId(@PathVariable String accountId) throws ResourceNotFound {
        return cardService.findCardsByAccountId(accountId);
    }

    @GetMapping("/getCard/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    @PostAuthorize("authentication.name == returnObject.userId || hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Mono<CardDTO> findCardById(@PathVariable String cardId) throws ResourceNotFound {
        return cardService.findCardById(cardId);
    }

    @GetMapping("/getCard/cardNumber/{cardNumber}")
    @ResponseStatus(HttpStatus.OK)
    @PostAuthorize("authentication.name == returnObject.userId || hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Mono<Card> findCardByCardNumber(@PathVariable Long cardNumber) throws ResourceNotFound {
        return cardService.findCardByNumber(cardNumber);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("#card.userId == authentication.name || hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_SERVICE')")
    public Mono<Card> saveCard(@RequestBody Card card) throws BadFieldException, EmptyRequiredField, AlreadyExist, ServerErrorException {
        return cardService.saveCard(card);
    }

    @DeleteMapping("/{cardId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> deleteCardById(@PathVariable String cardId, @AuthenticationPrincipal Principal principal) throws ResourceNotFound, BadFieldException {
        return cardService.deleteCard(cardId, principal);
    }

}
