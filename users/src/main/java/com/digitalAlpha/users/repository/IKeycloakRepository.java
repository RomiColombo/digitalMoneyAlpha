package com.digitalAlpha.users.repository;

import com.digitalAlpha.users.exception.AlreadyExistException;
import com.digitalAlpha.users.exception.KeycloakErrorException;
import com.digitalAlpha.users.model.UserKeycloak;
import reactor.core.publisher.Mono;

public interface IKeycloakRepository {

    Mono<UserKeycloak> findById(String id);
    Mono<UserKeycloak> save(UserKeycloak userKeycloak) throws KeycloakErrorException, AlreadyExistException;
    Mono<Void> invalidateUser(String id) throws KeycloakErrorException;
    Mono<Void> logout(String id);

    Mono<Void> update(UserKeycloak userKeycloak, String id);
}