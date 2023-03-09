package com.digitalAlpha.transactions.repository;

import com.digitalAlpha.transactions.configuration.feign.OAuthFeignConfiguration;
import com.digitalAlpha.transactions.exception.ResourceNotFound;
import com.digitalAlpha.transactions.model.dto.CardDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "ms-cards", url="${feign.cards.url}", configuration = OAuthFeignConfiguration.class)
public interface ICardsFeignRepository {

    @GetMapping("cards/getCard/cardNumber/{cardNumber}")
    Mono<CardDTO> findCardByCardNumber(@PathVariable Long cardNumber) throws ResourceNotFound;

}
