package com.digitalAlpha.cards.service.impl;

import com.digitalAlpha.cards.exception.*;
import com.digitalAlpha.cards.model.Card;
import com.digitalAlpha.cards.model.dto.AccountDTO;
import com.digitalAlpha.cards.model.dto.CardDTO;
import com.digitalAlpha.cards.model.enums.AccountTypesEnum;
import com.digitalAlpha.cards.repository.IAccountsFeignRepository;
import com.digitalAlpha.cards.repository.ICardRepository;
import com.digitalAlpha.cards.service.ICardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
@Slf4j
public class CardService implements ICardService {


    private final ICardRepository repository;

    private final IAccountsFeignRepository accountsFeignRepository;

    private final ModelMapper modelMapper;

    private final ReactiveCircuitBreakerFactory cbFactory;

    @Override
    public Flux<CardDTO> findCardsByAccountId(String accountId) throws ResourceNotFound {
        return repository.findCardsByAccountId(accountId);
    }

   @Override
    public Mono<CardDTO> findCardById(String cardId) throws ResourceNotFound {
        return repository.findCardById(cardId).map(card -> modelMapper.map(card, CardDTO.class));
    }

    @Override
    public Mono<Card> findCardByNumber(Long cardNumber) throws ResourceNotFound {
        return repository.findCardByCardNumber(cardNumber).doOnNext(card -> {
            if (card == null) throw new ResourceNotFound("Card doesn't exist");
        });
    }

    @Override
    public Mono<Card> saveCard(Card card) throws EmptyRequiredField, BadFieldException, AlreadyExist, ServerErrorException {


        //Empty validations
        if (card.getCardNumber() == null){
            throw new EmptyRequiredField("Card number cannot be empty");
        }
        if (card.getExpirationDate() == null){
            throw new EmptyRequiredField("Expiration day cannot be empty");
        }
        if(card.getCode() == null) {
            throw new EmptyRequiredField("Security code cannot be empty");
        }
        if(card.getFullName() == null){
            throw new EmptyRequiredField("Full name");
        }
        //Card validations
        validateCard(card.getCardNumber(), card.getExpirationDate(), card.getCode());

        Mono<Boolean> res = existCardByCardNumber(card.getCardNumber()).map(exist ->{
            if (exist){
                throw new AlreadyExist("This card already exists. Please try a new one.");
            }
            return false;
        });

        Mono<AccountDTO> account = res.thenMany(accountsFeignRepository.findAccountsByUserId(card.getUserId())
                        .transform(it -> cbFactory.create("findAccountsByUserId")
                                .run(it, throwable -> {
                                    log.error(throwable.getMessage());
                                    throw new ServerErrorException("The account service is unavailable at the moment. Please retry later");
                                })))
                .filter(a -> a.getId().equals(card.getAccountId()))
                .collectList().map(list -> {
                    if (list.isEmpty())
                        throw new BadFieldException("There is not an account with id No°" + card.getAccountId());
                    return list.get(0);
                });

        Mono<Tuple2<AccountDTO, Card>> data = account.zipWith(Mono.just(card));

        return data.flatMap(t -> {
            AccountDTO accountDTO = t.getT1();
            Card card1 = t.getT2();
            if(accountDTO.getType().equals(AccountTypesEnum.CA)) {
                card1.setAccountId(card.getAccountId());
                card1.setCompanyName(getCompany(card1.getCardNumber()));
            }
            if(accountDTO.getType().equals(AccountTypesEnum.CC)) {
                card1.setAccountId(card.getAccountId());
                card1.setCompanyName(getCompany(card1.getCardNumber()));
            }
            return repository.saveCard(card1);
        });
    }

    @Override
    public Mono<Void> deleteCard(String cardId,Principal principal) throws ResourceNotFound, BadFieldException {
        Mono<Tuple2<Card,Boolean>> validations = repository.findCardById(cardId).zipWith(hasAuthority()).doOnNext(t ->  {
            if (!t.getT2()) {
                if (!t.getT1().getUserId().equals(principal.getName())) throw new AccessDeniedException("Denied");
            }
        });
        return validations.then(repository.DeleteCard(cardId)).then();
    }


    private Mono<Boolean> existCardByCardNumber(Long cardNumber){
        return repository.findCardByCardNumber(cardNumber).map(Objects::nonNull);
    }

    private void validateCard(Long cardNumber, String dateInput, Integer cardCode) throws BadFieldException {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/yy");

        LocalDate currentDate = LocalDate.now();
        String dateFormat = currentDate.format(dateFormatter);

        if (dateInput.length()!=5){
            throw new BadFieldException("Invalid expiration date");
        }

        int currentMonth = Integer.parseInt(dateFormat.substring(0, 2));
        int currentYear = Integer.parseInt(dateFormat.substring(3, 5));

        int month = Integer.parseInt(dateInput.substring(0, 2));
        int year = Integer.parseInt(dateInput.substring(3, 5));
        //Validations
        if(cardCode.toString().length()!=3){
            throw new BadFieldException("Invalid CVV");        }
        if(cardNumber.toString().length()<15 || cardNumber.toString().length()>=17){
            throw new BadFieldException("Invalid card number");
        }
        if((month<currentMonth && year<currentYear) || (year<currentYear)){
            throw new BadFieldException("Expired date");
        }

        if(month<1 || month>=13){
            throw new BadFieldException("Invalid month");
        }
    }


    private String getCompany(Long cardNumber) throws BadFieldException {

        String company;

         //Company name
        if(cardNumber.toString().startsWith("4") & cardNumber.toString().length()==16){
            company = "Visa";
        }
        else if (cardNumber.toString().startsWith("5")) {
            company = "Mastercard";
        }
        else if (cardNumber.toString().startsWith("4") & cardNumber.toString().length()==15 || cardNumber.toString().startsWith("3") & cardNumber.toString().length()==16) {
            company = "American Express";
        }
        else{
            throw new BadFieldException("Invalid card number");
        }

        return company;

    }


    private Mono<Boolean> hasAuthority() {
        return ReactiveSecurityContextHolder.getContext()
                .map(a ->a.getAuthentication().getAuthorities().stream().anyMatch(auth ->
                        auth.getAuthority().equals("ROLE_ADMIN")
                                || auth.getAuthority().equals("ROLE_SERVICE")));
    }

}
