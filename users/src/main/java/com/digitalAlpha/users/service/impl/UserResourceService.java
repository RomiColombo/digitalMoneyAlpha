package com.digitalAlpha.users.service.impl;

import com.digitalAlpha.users.exception.*;
import com.digitalAlpha.users.model.UserKeycloak;
import com.digitalAlpha.users.model.dto.AccountDTO;
import com.digitalAlpha.users.model.dto.TransactionDTO;
import com.digitalAlpha.users.model.dto.UserAccountDTO;
import com.digitalAlpha.users.model.dto.UserRegisterDTO;
import com.digitalAlpha.users.repository.IAccountsFeignRepository;
import com.digitalAlpha.users.repository.IKeycloakRepository;
import com.digitalAlpha.users.repository.ITransfersFeignRepository;
import com.digitalAlpha.users.service.IUserResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class UserResourceService implements IUserResourceService {


    private final IKeycloakRepository keycloakRepository;
    private final IAccountsFeignRepository accountsFeignRepository;
    private final ITransfersFeignRepository transfersFeignRepository;
    private final ReactiveCircuitBreakerFactory cbFactory;
    private final ModelMapper modelMapper;


    @Override
    public Mono<UserAccountDTO> saveUser(UserRegisterDTO userRegistrationDTO) throws KeycloakErrorException, EmptyRequiredFieldException, ServerErrorException, AlreadyExistException {

        if (userRegistrationDTO.getEmail() == null) {
            throw new EmptyRequiredFieldException("email");
        } else {
            Pattern pattern = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
            if (!pattern.matcher(userRegistrationDTO.getEmail()).find()) {
                throw new BadFieldException("invalid mail");
            }
        }
        if (userRegistrationDTO.getDni() == null) {
            throw new EmptyRequiredFieldException("dni");
        }
        if (userRegistrationDTO.getPassword() == null) {
            throw new EmptyRequiredFieldException("password");
        }

        log.info(userRegistrationDTO.toString());

        // Save user to keycloak DB
        UserKeycloak userKeycloak = UserKeycloak.builder()
                .firstname(userRegistrationDTO.getFirstname())
                .lastname(userRegistrationDTO.getLastname())
                .dni(userRegistrationDTO.getDni())
                .phone(userRegistrationDTO.getPhone())
                .username(userRegistrationDTO.getEmail())
                .password(userRegistrationDTO.getPassword())
                .email(userRegistrationDTO.getEmail())
                .build();
        Mono<UserKeycloak> userKeycloakSaved = keycloakRepository.save(userKeycloak);

        Mono<Tuple2<UserKeycloak,AccountDTO<?>>> res = userKeycloakSaved.zipWhen(user -> {
            AccountDTO<?> account = new AccountDTO<>();
            account.setUserId(user.getId());
            LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("userId", user.getId());
            map.add("type", "CA");
            return accountsFeignRepository.save(map)
                    .transform(it -> cbFactory.create("save")
                            .run(it, throwable ->  Mono.just(AccountDTO.builder().isValid(false).build())));
        });

        return res.map(t -> {
            UserKeycloak user = t.getT1();
            AccountDTO<?> acc = t.getT2();
            UserAccountDTO userAccountDTO = modelMapper.map(user, UserAccountDTO.class);
            List<AccountDTO<?>> accountDTOList = new ArrayList<>();
            accountDTOList.add(acc);
            userAccountDTO.setAccounts(accountDTOList);
            return userAccountDTO;
        });
    }


    @Override
    public Mono<Void> invalidateUser(String id) throws ResourceNotFoundException, KeycloakErrorException, ConflictException {

        Mono<UserKeycloak> user = keycloakRepository.findById(id).map(user1 ->{
            if (user1 == null) {
                throw new ResourceNotFoundException("User with ID " + id + " doesn't exist");
            }
            return user1;
        });

        Flux<String> invalidateAccounts = user.thenMany(accountsFeignRepository.findAccountsByUserId(id))
                .transform(it -> cbFactory.create("findAccountsByUserId")
                        .run(it, throwable -> {
                            throw new ServerErrorException("The account service is unavailable at the moment. Please retry later");
                        }))
                .flatMap(acc -> accountsFeignRepository.invalidateAccount(acc.getId()));
        Mono<Void> invalidateUser = invalidateAccounts.then(keycloakRepository.invalidateUser(id))
                .doOnError( e-> {
            throw new ConflictException("There was a problem while invalidating user ");
        });
        invalidateUser.doOnSuccess(e -> cleanCache());

        return invalidateUser;
    }

    @Override
    public Mono<UserAccountDTO> getUser(String id) {
        Map<String,String> params = new HashMap<>();
        params.put("limit","5");
        params.put("sort","time-DESC");
        return cacheUser(id)
                .zipWhen(e -> accountsFeignRepository.findAccountsByUserId(id)
                        .transform(it -> cbFactory.create("findAccountsByUserId")
                                .run(it, throwable ->  Flux.just(AccountDTO.builder().isValid(false).build()))).collectList())
                .map(t ->{
                    UserAccountDTO us = modelMapper.map(t.getT1(), UserAccountDTO.class);
                    us.setAccounts(t.getT2());
                    return us;
                })
                .zipWhen(e -> transfersFeignRepository.getTransactionsByUserId(id,params)
                        .transform(it -> cbFactory.create("getTransactionsByUserId")
                                .run(it, throwable ->  Flux.just(TransactionDTO.builder().id("false").build()))).collectList())
                .map(t -> {
                    UserAccountDTO us = t.getT1();
                    us.setTransactions(t.getT2());
                    return us;
                });
    }

    @Override
    public Mono<Void> logout(String id) {
        return keycloakRepository.logout(id);
    }

    @Override
    public Mono<Void> modifyUser(UserKeycloak userKeycloak, String id) throws ResourceNotFoundException, ConflictException {

        if (userKeycloak.getDni() != null) {
            throw new ConflictException("Contact with your manager to modify dni");
        }

        if (userKeycloak.getPassword() != null) {
            throw new ConflictException("The password was not modified. Please try Forgot My Password in Login");
        }

        Mono<UserKeycloak> user = keycloakRepository.findById(id).doOnNext(us ->{
            if (us == null) {
                throw new ResourceNotFoundException("Error modifying user");
            }
        });

        return user.zipWhen(u -> {
            UserKeycloak updatedUser = verifyUser(userKeycloak, u);
            return keycloakRepository.update(updatedUser, id);
        }).map(Tuple2::getT1).then();

    }

    @Cacheable("userInfo")
    public Mono<UserKeycloak> cacheUser(String id) {
        return keycloakRepository.findById(id).cache();
    }

    @CacheEvict("userInfo")
    public void cleanCache() {
    }

    private UserKeycloak verifyUser(UserKeycloak userKeycloak, UserKeycloak user){

        UserKeycloak updatedUser = new UserKeycloak();

        if (userKeycloak.getUsername() == null) {
            updatedUser.setUsername(user.getUsername());
        } else {
            updatedUser.setUsername(userKeycloak.getUsername());
        }

        updatedUser.setPassword(user.getPassword());

        if (userKeycloak.getEmail() == null) {
            updatedUser.setEmail(user.getEmail());
        } else {
            updatedUser.setEmail(userKeycloak.getEmail());
        }

        if (userKeycloak.getFirstname() == null) {
            updatedUser.setFirstname(user.getFirstname());
        } else {
            updatedUser.setFirstname(userKeycloak.getFirstname());
        }

        if (userKeycloak.getLastname() == null) {
            updatedUser.setLastname(user.getLastname());
        } else {
            updatedUser.setLastname(userKeycloak.getLastname());
        }

        updatedUser.setDni(user.getDni());

        if (userKeycloak.getPhone() != null) {
            updatedUser.setPhone(userKeycloak.getPhone());
        } else {
            updatedUser.setPhone(user.getPhone());
        }

        return  updatedUser;
    }
}
