package com.digitalAlpha.users.controller;

import com.digitalAlpha.users.exception.*;
import com.digitalAlpha.users.model.UserKeycloak;
import com.digitalAlpha.users.model.dto.UserAccountDTO;
import com.digitalAlpha.users.model.dto.UserRegisterDTO;
import com.digitalAlpha.users.service.IUserResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final IUserResourceService service;


    @PostMapping(consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserAccountDTO> saveUser(UserRegisterDTO userRegisterDTO) throws ResourceConflictException, KeycloakErrorException, ResourceBadRequestException, EmptyRequiredFieldException, ServerErrorException, AlreadyExistException {
        return service.saveUser(userRegisterDTO);
    }

    @PatchMapping(value="/{id}", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("authentication.name == #id || hasAuthority('ROLE_ADMIN')")
    public Mono<Void> modifyUser(UserKeycloak userKeycloak, @PathVariable("id") String userId) throws ResourceNotFoundException, ConflictException {
        return service.modifyUser(userKeycloak, userId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("authentication.name == #id || hasAuthority('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> deleteById(@PathVariable String id) throws ResourceNotFoundException, KeycloakErrorException, ConflictException {
        return service.invalidateUser(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("authentication.name == #id || hasAuthority('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserAccountDTO> getById(@PathVariable String id) {
        return service.getUser(id);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> logoutUser(Principal principal) {
        return service.logout(principal.getName());
    }
}
