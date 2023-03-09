package com.digitalAlpha.accounts.controller;

import com.digitalAlpha.accounts.service.impl.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@Slf4j
@SuppressWarnings("all")
@WithMockUser(roles = "SERVICE")
@AutoConfigureMockMvc(addFilters = false)
@WebFluxTest(AdminController.class)
public class  AdminControllerTest {

    private static final String USER_UUID = "123456";
    private static final String ACCOUNT_UUID = "123456789";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AccountService accountService;

//    @Test
//    public void ValidateAccountByUserId_SuccessfullyTest() throws Exception {
//        when(accountService.validateAccountByUserId(any(String.class), any(String.class))).thenReturn(Mono.just(true));
//        webTestClient.get()
//                .uri("/admin/account/{accountId}/validate/{userId}",ACCOUNT_UUID,USER_UUID)
//                .exchange()
//                .expectStatus().isOk();
//    }
}
