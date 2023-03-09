package com.digitalAlpha.cards.repository.impl;

import com.digitalAlpha.cards.exception.BadFieldException;
import com.digitalAlpha.cards.exception.ResourceNotFound;
import com.digitalAlpha.cards.exception.ServerErrorException;
import com.digitalAlpha.cards.model.Card;
import com.digitalAlpha.cards.model.dto.CardDTO;
import com.digitalAlpha.cards.repository.ICardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CardRepository implements ICardRepository {

    private final ReactiveMongoTemplate template;

    private final String COLLECTION = "cards";

    @Override
    public Flux<CardDTO> findCardsByAccountId(String accountId) throws ResourceNotFound {
        Query query = new Query(Criteria.where("accountId").is(accountId));
        return template.find(query, CardDTO.class, COLLECTION).doOnError(onError()).collectList().flatMapIterable(cards -> {
                    if(cards.size()==0) {
                        throw new ResourceNotFound("No cards associated with account N°: "+accountId);
                    }
                    return cards;
                });
    }

    @Override
    public Mono<Card> findCardById(String id) throws ResourceNotFound {
        Query query = new Query(Criteria.where("id").is(id));
        return template.findOne(query, Card.class, COLLECTION).doOnError(onError()).doOnSuccess(card ->{
            if(card == null) {
                throw new ResourceNotFound("No cards associated with account N°: "+ id);
            }
        });
    }

    @Override
    public Mono<Card> saveCard(Card card) throws ServerErrorException {
        return template.insert(card,COLLECTION).doOnError(onError());
    }

    @Override
    public Mono<Boolean> DeleteCard(String id) throws BadFieldException, ResourceNotFound {
        Query query = new Query(Criteria.where("id").is(id));
        return template.find(query, Card.class, COLLECTION).doOnError(onError())
                .collectList().map( cards -> {
                    if (cards.size() == 0) throw new ResourceNotFound("Could not find this card");
                    if (cards.size() > 1) throw new BadFieldException("there was an error, please contact support");
                    return true;
                });
    }

    @Override
    public Mono<Card> findCardByCardNumber(Long cardNumber) {
        Query query = new Query(Criteria.where("cardNumber").is(cardNumber));
        return template.findOne(query, Card.class, COLLECTION);
    }

    private Consumer<Throwable> onError(){
        return e -> {
            throw new ServerErrorException("contact with support of digital Alpha");
        };
    }
}
