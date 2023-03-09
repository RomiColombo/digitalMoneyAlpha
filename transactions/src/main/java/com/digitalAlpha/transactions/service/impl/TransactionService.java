package com.digitalAlpha.transactions.service.impl;

import com.digitalAlpha.transactions.exception.*;
import com.digitalAlpha.transactions.model.dto.*;
import com.digitalAlpha.transactions.model.Transaction;
import com.digitalAlpha.transactions.model.enums.TransactionStatus;
import com.digitalAlpha.transactions.model.enums.TransactionType;
import com.digitalAlpha.transactions.repository.IAccountFeignRepository;
import com.digitalAlpha.transactions.repository.ICardsFeignRepository;
import com.digitalAlpha.transactions.repository.ITransactionRepository;
import com.digitalAlpha.transactions.service.ITransactionService;
import com.digitalAlpha.transactions.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Slf4j
@Service
@SuppressWarnings("rawtypes")
public class TransactionService implements ITransactionService {

    public static final double TRANSACTION_FEE = 0.05;

    public static final double DEPOSIT_FEE = 0.0;

    private final ITransactionRepository repository;

    private final ICardsFeignRepository cardsFeignRepository;

    private final IAccountFeignRepository accountFeignRepository;

    private final ReactiveCircuitBreakerFactory cbFactory;

    @Value("${spring.external.service.base-url}")
    private String basePath;

    @Value("${external.api-key}")
    private String rapidApiKey;

    @Autowired
    public TransactionService(ITransactionRepository repository, ICardsFeignRepository cardsFeignRepository, IAccountFeignRepository accountFeignRepository, ReactiveCircuitBreakerFactory cbFactory) {
        this.repository = repository;
        this.cardsFeignRepository = cardsFeignRepository;
        this.accountFeignRepository = accountFeignRepository;
        this.cbFactory = cbFactory;
    }

    @Override
    public Mono<TransactionDTO> getById(String id) throws EmptyRequiredField, ResourceNotFound {
        if (id == null) throw new EmptyRequiredField("Can't find transaction with id null");
        return repository.getById(id).map(AppUtils::entityToDto);
    }


    @Override
    public Mono<Void> createTransaction(PublishTransactionDTO publish) throws EmptyRequiredField, InvalidTransaction {

        TransactionRequestDTO transaction = publish.getKafkaTransactionRequestDTO();

        Double[] amounts = feeTransaction(transaction.getAmount());

        Transaction originTransaction = Transaction.builder()
                .id(publish.getMessageKey())
                .type(TransactionType.OUT)
                .description(transaction.getDescription())
                .amount(amounts[0])
                .accountTo(transaction.getCvuTo())
                .accountFrom(transaction.getCvuFrom())
                .fee(TRANSACTION_FEE)
                .status(TransactionStatus.PENDING)
                .time(LocalDateTime.now())
                .userId(transaction.getUserId())
                .build();
        Mono<Transaction> savedTransaction = repository.createTransaction(originTransaction);

        Mono<Transaction> res = savedTransaction.zipWhen(t -> {
            UpdateAmountDTO update = UpdateAmountDTO.builder()
                    .accountFrom(t.getAccountFrom())
                    .accountTo(t.getAccountTo())
                    .accountFromAmount(amounts[0])
                    .accountToAmount(amounts[1])
                    .build();
            return accountFeignRepository.transactionalUpdate(update)
                    .transform(it -> cbFactory.create("transactionalUpdate")
                            .run(it, throwable -> {
                                repository.updateStatus(publish.getMessageKey(), TransactionStatus.FAIL);
                                throw new ServerErrorException("The account service is unavailable at this moment. Please retry later");
                            }));
        }).flatMap(tuple -> {
            if (!tuple.getT2()) {
                return repository.updateStatus(tuple.getT1().getId(), TransactionStatus.FAIL)
                        .doOnNext(e -> log.error("Can't complete transaction"));
            }
            return repository.updateStatus(tuple.getT1().getId(), TransactionStatus.APPROVED)
                    .onErrorMap(e -> {
                        log.error("Error updating status " + e.getMessage());
                        return e;
                    });
        });

        Mono<Tuple2<Transaction, Transaction>> destinationTransaction = res.zipWith(Mono.just(Transaction.builder()
                .type(TransactionType.IN)
                .description(transaction.getDescription())
                .amount(amounts[1])
                .accountTo(transaction.getCvuFrom())
                .accountFrom(transaction.getCvuTo())
                .fee(0.0)
                .status(TransactionStatus.APPROVED)
                .time(LocalDateTime.now())
                .userId(transaction.getUserIdTo())
                .build()));

        return destinationTransaction.flatMap(t -> {
            if (!t.getT1().getStatus().equals(TransactionStatus.FAIL)) {
                return repository.createTransaction(t.getT2());
            }
            return Mono.empty();
        }).then();
    }

    @Override
    public Mono<Void> createDeposit(PublishTransactionDTO publish) throws
            InvalidTransaction, ResourceNotFound, EmptyRequiredField, ServerErrorException {

        DepositDTO depositDTO = publish.getDepositDTO();

        if (depositDTO.getAmount() <= 0) {
            throw new InvalidTransaction("Invalid amount");
        }

        Mono<CardDTO> card = cardsFeignRepository.findCardByCardNumber(depositDTO.getCardNumber())
                .transform(it -> cbFactory.create("findCardByCardNumber")
                        .run(it, throwable -> {
                            throw new ServerErrorException("The cards service is unavailable at this moment. Please retry later");
                        }));


        Mono<Tuple2<CardDTO, Transaction>> validations = card.zipWhen(cardDTO -> {
            if (!(depositDTO.getAccountId().equals(cardDTO.getAccountId()))) {
                throw new InvalidTransaction("Invalid account id");
            }
            if (!(cardDTO.getUserId().equals(depositDTO.getUserId()))) {
                throw new InvalidTransaction("Invalid user id");
            }
            Transaction transaction = Transaction.builder()
                    .id(publish.getMessageKey())
                    .type(TransactionType.DEPOSIT)
                    .amount(depositDTO.getAmount())
                    .accountTo(cardDTO.getAccountId())
                    .fee(DEPOSIT_FEE)
                    .status(TransactionStatus.PENDING)
                    .time(LocalDateTime.now())
                    .userId(depositDTO.getUserId())
                    .build();
            return repository.createTransaction(transaction);
        });

        return validations.flatMap(tuple -> externalAPIConnection(depositDTO, tuple.getT1())
                .doOnError(e -> {
                    repository.updateStatus(tuple.getT2().getId(), TransactionStatus.FAIL).doOnNext(err -> {
                        throw new ServerErrorException("External cards service is unavailable at this moment. Please retry later");
                    });
                }).flatMap(t -> {
                    AccountRequestDTO accountRequestDTO = AccountRequestDTO.builder()
                            .amount(depositDTO.getAmount())
                            .type(TransactionType.DEPOSIT)
                            .build();
                    return accountFeignRepository.updateAmount(depositDTO.getAccountId(), accountRequestDTO)
                            .transform(it -> cbFactory.create("updateAmount")
                                    .run(it, throwable -> {
                                        repository.updateStatus(tuple.getT2().getId(), TransactionStatus.FAIL);
                                        throw new ServerErrorException("The cards service is unavailable at the moment. Please retry later");
                                    }))
                            .flatMap(tu -> repository.updateStatus(tuple.getT2().getId(), TransactionStatus.APPROVED))
                            .then();
                }));
    }


    @Override
    public Flux<ReducedTransactionDTO> getTransactionsByUserId(String id, Map<String, String> params) {
        Map<String, Object> filters = new HashMap<>();
        if (params.get("limit") != null) {
            try {
                Integer limit = Integer.parseInt(params.get("limit"));
                if (limit > 50) {
                    filters.put("limit", 50);
                } else {
                    filters.put("limit", limit);
                }
            } catch (Exception ex) {
                throw new BadField("can't find a valid amount, try again.");
            }
        }
        if (params.get("amount") != null) {
            try {
                List<Double> range = Arrays.stream(params.get("amount").split("-")).map(Double::valueOf).toList();
                if (range.size() == 0) throw new BadField("amount param can't be empty");
                if (range.size() > 2) throw new BadField("amount param can't have more than two values");
                if (range.size() == 1) {
                    filters.put("amount", List.of(0.0, range.get(0)));
                } else {
                    if (Objects.equals(range.get(0), range.get(1)))
                        throw new BadField("amount param range is equal, not result find");
                    if (range.get(0) > range.get(1))
                        throw new BadField("first amount can't be higher than second amount");
                    filters.put("amount", range);
                }
            } catch (Exception e) {
                throw new BadField("can't find a valid amount, try again.");
            }
        }
        if (params.get("time") != null) {
            try {
                List<LocalDateTime> range = Arrays.stream(params.get("time").split("-")).map(LocalDateTime::parse).toList();
                if (range.size() == 0) throw new BadField("time param can't be empty");
                if (range.size() > 2) throw new BadField("time param can't have more than two values");
                if (range.size() == 1) {
                    LocalDateTime first = LocalDateTime.parse(params.get("time")).minusHours(3);
                    List<LocalDateTime> local = List.of(first, range.get(0));
                    filters.put("time", local);
                } else {
                    if (range.get(0).equals(range.get(1)))
                        throw new BadField("time param range is equal, not result find");
                    if (range.get(0).isAfter(range.get(1)))
                        throw new BadField("first date can't be higher than second time");
                    filters.put("time", range);
                }
            } catch (Exception e) {
                throw new BadField("can't find a valid time, try again.");
            }
        }
        if (params.get("type") != null) {
            try {
                TransactionType type = TransactionType.valueOf(params.get("type"));
                filters.put("type", type);
            } catch (Exception e) {
                throw new BadField("can't find a valid type, try again.");
            }
        }
        if (params.get("status") != null) {
            try {
                TransactionStatus status = TransactionStatus.valueOf(params.get("status"));
                filters.put("status", status);
            } catch (Exception ex) {
                throw new BadField("can't find a valid status, try again.");
            }
        }
        if (params.get("page") != null) {
            try {
                Integer page = Integer.parseInt(params.get("page"));
                filters.put("page", page);
            } catch (Exception ex) {
                throw new BadField("can't find a valid amount, try again.");
            }
        }

        if (params.get("cvu") != null) {
            try {
                String cvu = params.get("cvu");
                filters.put("cvu", cvu);
            } catch (Exception ex) {
                throw new BadField("can't find a valid account, try again.");
            }
        }

        if (params.get("sort") != null) {
            try {
                List<String> options = List.of(new String[]{"type", "time", "status", "amount"});
                List<String> ways = List.of(new String[]{"ASC", "DESC"});
                List<String> sort = Arrays.stream(params.get("sort").split("-")).toList();
                if (sort.size() == 0) throw new BadField("sort param can't be empty");
                if (sort.size() == 1) throw new BadField("sort param can't need property and order");
                if (!options.contains(sort.get(0))) throw new BadField("sort param have invalid property");
                if (!ways.contains(sort.get(1)))
                    throw new BadField("sort param have invalid sort way, ASC and DESC are valid");
                filters.put("sort", sort);
            } catch (Exception e) {
                throw new BadField("can't find a valid date, try again.");
            }
        }

        Flux<ReducedTransactionDTO> transactions = repository.getByFilters(filters, id);

        transactions.collect(Collectors.toList()).doOnSuccess(t -> {
            if (t.size() == 0)
                throw new ResourceNotFound("There is not any match for the requested filters");
        });

        return transactions;

    }

    private Mono<ExternalResponseDTO> externalAPIConnection(DepositDTO depositDTO, CardDTO card) throws
            ServerErrorException {

        MultiValueMap<String, String> bodyForm = new LinkedMultiValueMap<>();
        bodyForm.add("cardNumber", depositDTO.getCardNumber().toString());
        bodyForm.add("code", card.getCode().toString());
        bodyForm.add("expirationDate", card.getExpirationDate());
        bodyForm.add("amount", String.valueOf(depositDTO.getAmount()));

        WebClient webClient = WebClient.create(basePath);

        Mono<ExternalResponseDTO> response = webClient.post()
                .uri("/transaction")
                .header("api-key", rapidApiKey)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(bodyForm))
                .retrieve()
                .bodyToMono(ExternalResponseDTO.class);

        return response;

    }

    @Override
    public Mono<TransactionStatus> getStatus(String id) {
        return repository.getStatus(id);
    }

    private Double[] feeTransaction(Double amount) {
        Double amount1 = amount + (amount * TRANSACTION_FEE);
        return new Double[]{amount1, amount};
    }


}
