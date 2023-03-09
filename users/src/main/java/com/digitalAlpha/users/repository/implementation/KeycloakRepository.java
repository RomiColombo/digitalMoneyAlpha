package com.digitalAlpha.users.repository.implementation;

import com.digitalAlpha.users.exception.AlreadyExistException;
import com.digitalAlpha.users.exception.KeycloakErrorException;
import com.digitalAlpha.users.model.UserKeycloak;
import com.digitalAlpha.users.repository.IKeycloakRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.ws.rs.core.Response;
import java.util.*;


@Repository
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class KeycloakRepository implements IKeycloakRepository {

    @Autowired
    private Keycloak keycloakClient;

    @Value("${keycloak.realm}")
    private String realm;


    @Override
    public Mono<UserKeycloak> findById(String id) {
        UserRepresentation userRepresentation = keycloakClient.realm(realm).users().get(id).toRepresentation();
        if (!userRepresentation.isEnabled()) throw new KeycloakErrorException("user not found");
        UserKeycloak user = UserKeycloak.builder()
                .dni(String.valueOf(userRepresentation.getAttributes().get("dni").get(0)))
                .phone(String.valueOf(userRepresentation.getAttributes().get("phone").get(0)))
                .firstname(userRepresentation.getFirstName())
                .lastname(userRepresentation.getLastName())
                .email(userRepresentation.getEmail())
                .username(userRepresentation.getUsername())
                .build();
        return Mono.just(user);
    }

    @Override
    public Mono<UserKeycloak> save(UserKeycloak userKeycloak) throws KeycloakErrorException, AlreadyExistException {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(userKeycloak.getPassword());
        credentialRepresentation.setTemporary(false);

        UserRepresentation userRepresentationToSave = new UserRepresentation();
        userRepresentationToSave.setEmail(userKeycloak.getEmail());
        userRepresentationToSave.setUsername(userKeycloak.getUsername());
        userRepresentationToSave.setCredentials(Collections.singletonList(credentialRepresentation));
        userRepresentationToSave.setFirstName(userKeycloak.getFirstname());
        userRepresentationToSave.setLastName(userKeycloak.getLastname());

        Map<String,List<String>> attributes = new HashMap<>();
        attributes.put("dni", Collections.singletonList(userKeycloak.getDni()));
        attributes.put("phone", Collections.singletonList(userKeycloak.getPhone()));

        userRepresentationToSave.setAttributes(attributes);

        userRepresentationToSave.setEnabled(true);

        Response response = keycloakClient.realm(realm).users().create(userRepresentationToSave);

        if (response.getStatus() != 201) {
            if (response.getStatus() == 409) throw new AlreadyExistException("Already exist a user with that email");
            throw new KeycloakErrorException("There was a error creating a user");
        }

        String userId = CreatedResponseUtil.getCreatedId(response);

        UserKeycloak userKeycloakSaved = UserKeycloak.builder()
                .id(userId)
                .username(userRepresentationToSave.getUsername())
                .email(userRepresentationToSave.getEmail())
                .firstname(userRepresentationToSave.getFirstName())
                .lastname(userRepresentationToSave.getLastName())
                .dni(userKeycloak.getDni())
                .phone(userKeycloak.getPhone())
                .build();

        return Mono.just(userKeycloakSaved);
    }

    @Override
    public Mono<Void> invalidateUser(String id) throws KeycloakErrorException {
        return Mono.just(keycloakClient.realm(realm).users().get(id).toRepresentation()).flatMap(ur -> {
            if (ur == null){
                throw new KeycloakErrorException("Error while deleting user, user not found");
            }
            ur.setEnabled(false);
            try {
                keycloakClient.realm(realm).users().get(id).update(ur);
            }catch (Exception ex){
                throw new KeycloakErrorException("Error while deleting user");
            }
            return Mono.empty();
        });
    }

    @Override
    public Mono<Void> logout(String id){
        keycloakClient.realm(realm).users().get(id).logout();
        return Mono.empty();
    }


    @Override
    public Mono<Void> update(UserKeycloak userKeycloak, String id) {

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();

        if (userKeycloak.getPassword() != null ){
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(userKeycloak.getPassword());
        credentialRepresentation.setTemporary(false);
        }

        UserRepresentation userRepresentationToSave = new UserRepresentation();

        if (userKeycloak.getEmail() != null)
            userRepresentationToSave.setEmail(userKeycloak.getEmail());

        if (userKeycloak.getUsername() != null)
            userRepresentationToSave.setUsername(userKeycloak.getUsername());

        if (userKeycloak.getFirstname() != null)
            userRepresentationToSave.setFirstName(userKeycloak.getFirstname());

        if (userKeycloak.getLastname() != null)
            userRepresentationToSave.setLastName(userKeycloak.getLastname());

        if (credentialRepresentation.getValue() != null)
            userRepresentationToSave.setCredentials(Collections.singletonList(credentialRepresentation));


        Map<String,List<String>> attributes = new HashMap<>();
        attributes.put("dni", Collections.singletonList(userKeycloak.getDni()));
        attributes.put("phone", Collections.singletonList(userKeycloak.getPhone()));

        userRepresentationToSave.setAttributes(attributes);

        keycloakClient.realm(realm).users().get(id).update(userRepresentationToSave);
        return Mono.empty();
    }
}