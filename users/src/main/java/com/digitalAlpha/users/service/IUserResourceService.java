package com.digitalAlpha.users.service;

import com.digitalAlpha.users.exception.*;
import com.digitalAlpha.users.model.UserKeycloak;
import com.digitalAlpha.users.model.dto.UserAccountDTO;
import com.digitalAlpha.users.model.dto.UserRegisterDTO;
import reactor.core.publisher.Mono;

public interface IUserResourceService {

    Mono<UserAccountDTO> saveUser(UserRegisterDTO userRegistrationDTO) throws ResourceBadRequestException, KeycloakErrorException, ResourceConflictException, EmptyRequiredFieldException, ServerErrorException, AlreadyExistException;

    Mono<UserAccountDTO> getUser(String id);

    Mono<Void> modifyUser(UserKeycloak userKeycloak, String userId) throws ResourceNotFoundException, ConflictException;

    Mono<Void> invalidateUser(String id) throws ResourceNotFoundException, KeycloakErrorException, ConflictException;

    Mono<UserKeycloak> cacheUser(String id);

    void cleanCache();

    Mono<Void> logout(String id);

}
