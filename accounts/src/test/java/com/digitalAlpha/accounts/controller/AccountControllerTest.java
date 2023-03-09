package com.digitalAlpha.accounts.controller;

import com.digitalAlpha.accounts.exception.AlreadyExist;
import com.digitalAlpha.accounts.exception.EmptyRequiredField;
import com.digitalAlpha.accounts.model.Account;
import com.digitalAlpha.accounts.model.AccountCA;
import com.digitalAlpha.accounts.model.enums.AccountTypesEnum;
import com.digitalAlpha.accounts.service.impl.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SuppressWarnings("all")
@WithMockUser(roles = "ADMIN",username = "123456")
@AutoConfigureMockMvc(addFilters = false)
@WebFluxTest(AccountController.class)
public class  AccountControllerTest {

    private static final String USER_UUID = "123456";
    private static final String ACCOUNT_UUID = "123456789";
    private static final Boolean IS_VALID_TRUE = true;
    private static final String ALIAS = "soy.un.alias";
    private static final AccountTypesEnum ACCOUNT_TYPE_ENUM = AccountTypesEnum.CA;
    private static final String ACCOUNT_TYPE_STRING = "CA";
    private static final Double AVAILABLE_AMOUNT = 100.0;
    private static final String CVU = "01234";

    private static final Principal PRINCIPAL = () -> USER_UUID;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AccountService accountService;

    @Test
    public void CreateAccount_SuccessfullyTest() throws Exception {

        // Given these instructions
        Account serviceResponse = buildExpectedResponse();

        when(accountService.save(any(String.class), any(String.class))).thenReturn(Mono.just(serviceResponse));

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).post()
                .uri("/accounts")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .attribute("userId", USER_UUID)
                .attribute("type",ACCOUNT_TYPE_STRING)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    public void FindAccountsByUserId_SuccessfullyTest() throws Exception {

        // Given these instructions
        Flux<Account> serviceResponse = Flux.just(buildExpectedResponse());
        when(accountService.getByUserId(any(String.class))).thenReturn(serviceResponse);

        webTestClient.get()
                .uri("/accounts/user/{userId}",USER_UUID)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void FindAccountsById_Successfully() throws Exception {

        // Given these instructions
        Account serviceResponse = buildExpectedResponse();
        when(accountService.getById(any(String.class))).thenReturn(Mono.just(serviceResponse));

        webTestClient.get()
                .uri("/accounts/{id}", ACCOUNT_UUID)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("{'id':'123456789','type':'CA',"+
                        "'userId':'123456','valid':true,"+
                        "'account':{'alias':'soy.un.alias','cvu':'01234','availableAmount':100.0,'currency':null}}");
    }

    @Test
    public void InvalidateAccountById_SuccessfullyTest() throws Exception {

        // Given these instructions
        when(accountService.invalidate(any(String.class), any(Principal.class))).thenReturn(Mono.empty());

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).delete()
                .uri("/accounts/{id}", ACCOUNT_UUID)
                .exchange()
                .expectStatus().isAccepted();
    }

    @Test
    public void findAccountByAlias_SuccessfullyTest() throws EmptyRequiredField {
        // Given these instructions
        when(accountService.getByAlias(any(String.class))).thenReturn(Mono.just(buildExpectedResponse()));

        webTestClient.get()
                .uri("/accounts/alias/{alias}", ALIAS)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void updateAlias_SuccessfullyTest() throws EmptyRequiredField, AlreadyExist {
        // Given these instructions
        when(accountService.updateAlias(any(String.class),any(String.class),any(String.class))).thenReturn(Mono.just(buildExpectedResponse()));

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).patch()
                .uri("/accounts/{id}", ACCOUNT_UUID)
                .contentType(MediaType.TEXT_PLAIN)
                .body(Mono.just("just.a.test"),String.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void findAccountByCVU_SuccessfullyTest() throws EmptyRequiredField {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).get()
                .uri("/accounts/cvu/{cvu}", CVU)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    private Account buildExpectedResponse() {
        AccountCA accountCA = AccountCA.builder()
                .alias(ALIAS)
                .cvu(CVU)
                .availableAmount(AVAILABLE_AMOUNT)
                .build();
        Account<?> account = Account.builder()
                .id(ACCOUNT_UUID)
                .type(AccountTypesEnum.CA)
                .userId(USER_UUID)
                .valid(IS_VALID_TRUE)
                .account(accountCA)
                .build();
        return account;
    }

}
