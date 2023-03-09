package com.digitalAlpha.accounts.service.impl;

import com.digitalAlpha.accounts.model.UpdateAmountDTO;
import com.digitalAlpha.accounts.repository.impl.AccountRepository;
import com.digitalAlpha.accounts.repository.impl.TransactionalAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;

@Slf4j
@SpringBootTest
@SuppressWarnings("all")
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN",username = "123456")
@AutoConfigureMockMvc(addFilters = false)
@EnableAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
public class TransactionalAccountServiceTest {

    @InjectMocks
    private TransactionalAccountService transactionalAccountService;

    @Mock
    private TransactionalAccountRepository transactionalRepository;
    @Mock
    private AccountRepository accountRepository;

    @Test
    public void UpdateAmount_SuccessfullyTest() {
        //Given these instructions
        UpdateAmountDTO updateAmountDTO = UpdateAmountDTO.builder()
                .accountTo(UUID.randomUUID().toString())
                .accountFrom(UUID.randomUUID().toString())
                .accountFromAmount(123.4)
                .accountToAmount(100.0)
                .build();
        Double initialFromAmount = updateAmountDTO.getAccountFromAmount();
        Double initialToAmount = updateAmountDTO.getAccountToAmount();

        Mockito.when(accountRepository.getAmountByCVU(any(String.class))).thenReturn(Mono.just(123.4));
        Mockito.when(transactionalRepository.updateAmount(any(UpdateAmountDTO.class))).thenReturn(Mono.just(Boolean.TRUE));

        //When this happens
        Mono<Boolean> serviceResponse = transactionalAccountService.updateAmount(updateAmountDTO);

        //Then assert
        Mockito.verify(accountRepository, Mockito.times(2)).getAmountByCVU(any(String.class));
        Mockito.verifyNoMoreInteractions(accountRepository);}

}
