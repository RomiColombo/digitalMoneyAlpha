package com.digitalAlpha.accounts.service.impl;

import com.digitalAlpha.accounts.exception.BadField;
import com.digitalAlpha.accounts.exception.EmptyRequiredField;
import com.digitalAlpha.accounts.exception.ResourceNotFound;
import com.digitalAlpha.accounts.model.Account;
import com.digitalAlpha.accounts.model.AccountCA;
import com.digitalAlpha.accounts.model.AccountCC;
import com.digitalAlpha.accounts.model.AccountRequestDTO;
import com.digitalAlpha.accounts.model.enums.AccountTypesEnum;
import com.digitalAlpha.accounts.model.enums.TransactionType;
import com.digitalAlpha.accounts.repository.impl.AccountRepository;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



@Slf4j
@SpringBootTest
@SuppressWarnings("all")
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN",username = "123456")
@AutoConfigureMockMvc(addFilters = false)
@EnableAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
public class AccountServiceTest {

    private static final String USER_UUID = UUID.randomUUID().toString();
    private static final String ACCOUNT_UUID = UUID.randomUUID().toString();
    private static final Boolean IS_VALID_TRUE = true;
    private static final String ALIAS = "soy.un.alias";
    private static final AccountTypesEnum ACCOUNT_TYPE_ENUM = AccountTypesEnum.CA;
    private static final String ACCOUNT_TYPE_STRING = "CA";
    private static final Double NO_AVAILABLE_AMMOUNT = 0.0;
    private static final Double POSITIVE_AVAILABLE_AMMOUNT = 10.0;
    private static final Double NEGATIVE_AVAILABLE_AMMOUNT = -10.0;
    private static final String CVU = "01234";

    private static final String EMPTY_ACCOUNT_ID_MSG = "Can't find account, id field is required";
    private static final String INVALID_ACCOUNT_TYPE = "something happened saving account";

    private static final Principal PRINCIPAL = () -> USER_UUID;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    public void CreateAccount_SuccessfullyTest() throws Exception {

        // Given these instructions
        Mono<Account<?>> expectedResponse = buildExpectedResponse(NO_AVAILABLE_AMMOUNT).map(acc -> (Account<?>) acc);
        Mockito.when(accountRepository.save(any(Account.class))).thenReturn(expectedResponse);
        Mockito.when(accountRepository.findAccountsByUserId(any(String.class))).thenReturn(Flux.empty());


        // When this happens
        Mono<Account<?>> response = accountService.save(USER_UUID, ACCOUNT_TYPE_STRING);
        Mono<Account<AccountCA>> finalResponse = response.map(acc ->(Account<AccountCA>) acc);
        Mono<Account<AccountCA>> finalExpectedResponse = expectedResponse.map(account -> (Account<AccountCA>) account);

        Account<AccountCA> accountRes = finalResponse.block();
        assert accountRes != null;
        AccountCA accountCARes = accountRes.getAccount();
        // Assert that
        assertThat(accountRes.getId()).isNotNull();
        assertThat(accountRes.getUserId()).isEqualTo(USER_UUID);
        assertThat(accountRes.getType()).isEqualTo(ACCOUNT_TYPE_ENUM);
        assertThat(accountRes.getAccount()).isEqualTo(expectedResponse.block().getAccount());

        assertThat(accountCARes.getAvailableAmount()).isEqualTo(finalExpectedResponse.block().getAccount().getAvailableAmount());
        assertThat(accountCARes.getAlias()).isEqualTo(finalExpectedResponse.block().getAccount().getAlias());
        assertThat(accountCARes.getCvu()).isEqualTo(finalExpectedResponse.block().getAccount().getCvu());

        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    public void CreateAccount_TypeCC_SuccessfullyTest() throws Exception {

        // Given these instructions
        Mono<Account<?>> expectedResponse = buildExpectedResponseCC(NO_AVAILABLE_AMMOUNT).map(acc ->(Account<?>) acc);
        Mockito.when(accountRepository.save(any(Account.class))).thenReturn(expectedResponse);
        Mockito.when(accountRepository.findAccountsByUserId(any(String.class))).thenReturn(Flux.empty());

        // When this happens
        Mono<Account<?>> response = accountService.save(USER_UUID, "CC");
        Mono<Account<AccountCC>> finalResponse = response.map(acc ->(Account<AccountCC>) acc);
        Mono<Account<AccountCC>> finalExpectedResponse = expectedResponse.map(account -> (Account<AccountCC>) account);

        Account<AccountCC> accountRes = finalResponse.block();
        assert accountRes != null;
        AccountCC accountCARes = (AccountCC) accountRes.getAccount();
        // Assert that
        assertThat(accountRes.getType()).isEqualTo(AccountTypesEnum.CC);


        verify(accountRepository, times(1)).save(any(Account.class));

    }
    @Test
    public void save_InvalidType_ThrowsServerErrorException() throws Exception {

        // When this happens, then assert
        assertThrows(BadField.class, () -> accountService.save(USER_UUID,"REFG"), INVALID_ACCOUNT_TYPE);

    }

    @Test
    public void FindAccountsByUserId_SuccessfullyTest() throws Exception {

        // Given these instructions
        Flux<Account> expectedResponse = buildExpectedResponse(NO_AVAILABLE_AMMOUNT).flux();
        Mockito.when(accountRepository.findAccountsByUserId(any(String.class))).thenReturn(expectedResponse);

        // When this happens
        Flux<Account> responseList = accountService.getByUserId(USER_UUID);
        Account<AccountCA> responseSingle = responseList.collectList().block().get(0);

        Account<AccountCA> expected = expectedResponse.collectList().block().get(0);
        // Then assert
        assertThat(responseSingle).isNotNull();
        assertThat(responseSingle.getId()).isEqualTo(expected.getId());
        assertThat(responseSingle.getUserId()).isEqualTo(expected.getUserId());
        assertThat(responseSingle.getAccount()).isEqualTo(expected.getAccount());
        assertThat(responseSingle.getType()).isEqualTo(expected.getType());

        assertThat(responseSingle.getAccount().getAvailableAmount()).isEqualTo(expected.getAccount().getAvailableAmount());
        assertThat(responseSingle.getAccount().getAlias()).isEqualTo(expected.getAccount().getAlias());
        assertThat(responseSingle.getAccount().getCvu()).isEqualTo(expected.getAccount().getCvu());

        verify(accountRepository, times(1)).findAccountsByUserId(any(String.class));

    }

    @Test
    public void FindAccountsByUserId_NullUserId_ThrowsEmptyRequiredFieldException() {

        // When this happens, then assert
        assertThrows(ResourceNotFound.class, () -> accountService.getByUserId(null), "Can't find username, field is null");

    }

    @Test
    public void FindAccountById_SuccessfullyTest() throws Exception {

        // Given these instructions
        Mono<Account> expectedRepositoryResponse = buildExpectedResponse(NO_AVAILABLE_AMMOUNT);
        Mockito.when(accountRepository.findById(any(String.class))).thenReturn(expectedRepositoryResponse);

        // When this happens
        Mono<Account> response = accountService.getById(ACCOUNT_UUID);
        Mono<Account<AccountCA>> finalResponse = response.map(account -> (Account<AccountCA>) account);
        Mono<Account<AccountCA>> expectedResponse = expectedRepositoryResponse.map(account -> (Account<AccountCA>) account);

        // Then assert
        finalResponse.doOnSuccess( res -> {
            assertThat(res).isNotNull();
            assertThat(res.getId()).isEqualTo(expectedResponse.block().getId());
            assertThat(res.getUserId()).isEqualTo(expectedResponse.block().getUserId());
            assertThat(res.getAccount()).isEqualTo(expectedResponse.block().getAccount());
            assertThat(res.getType()).isEqualTo(expectedResponse.block().getType());

            assertThat(res.getAccount().getAvailableAmount()).isEqualTo(expectedResponse.block().getAccount().getAvailableAmount());
            assertThat(res.getAccount().getAlias()).isEqualTo(expectedResponse.block().getAccount().getAlias());
            assertThat(res.getAccount().getCvu()).isEqualTo(expectedResponse.block().getAccount().getCvu());
        });
        verify(accountRepository, times(1)).findById(any(String.class));
    }

    @Test
    public void FindAccountById_NullId_ThrowsEmptyRequiredFieldException() throws Exception {

        // When this happens, then assert
        assertThrows(EmptyRequiredField.class, () -> accountService.getById(null), EMPTY_ACCOUNT_ID_MSG);

    }

    @Test
    public void FindAccountById_BlankId_ThrowsEmptyRequiredFieldException() throws Exception {

        // When this happens, then assert
        assertThrows(EmptyRequiredField.class, () -> accountService.getById(""), EMPTY_ACCOUNT_ID_MSG);

    }

    @Test
    public void InvalidateAccountById_SuccessfullyTest() throws Exception {

        // Given these instructions
        when(accountRepository.findById(any(String.class))).thenReturn(buildExpectedResponse(NO_AVAILABLE_AMMOUNT));
        when(accountRepository.invalidateById(any(String.class))).thenReturn(Mono.empty());

        // When this happens
        accountService.invalidate(ACCOUNT_UUID, PRINCIPAL);

        // Then assert
        verify(accountRepository, times(1)).findById(any(String.class));
        verify(accountRepository, times(1)).invalidateById(any(String.class));
        verifyNoMoreInteractions(accountRepository);

    }

    @Test
    public void InvalidateAccountById_NullId_ThrowsEmptyRequiredFieldException() throws Exception {

        // When this happens, then assert
        assertThrows(EmptyRequiredField.class, () -> accountService.invalidate(null,PRINCIPAL), EMPTY_ACCOUNT_ID_MSG);

    }

    @Test
    public void InvalidateAccountById_BlankId_ThrowsEmptyRequiredFieldException() throws Exception {

        // When this happens, then assert
        assertThrows(EmptyRequiredField.class, () -> accountService.invalidate("", PRINCIPAL), EMPTY_ACCOUNT_ID_MSG);

    }

    @Test
    public void InvalidateAccountById_PositiveAvailableAmmount_ThrowsServerErrorException() throws Exception {

        // Given these instructions
        Mono<Account> expectedRepositoryResponse = buildExpectedResponse(POSITIVE_AVAILABLE_AMMOUNT);
        when(accountRepository.findById(any(String.class))).thenReturn(expectedRepositoryResponse);

        // When this happens, then assert
        assertThrows(NullPointerException.class, () -> accountService.invalidate(expectedRepositoryResponse.block().getId(), PRINCIPAL));
        //verify(accountRepository, times(2)).findById(any(String.class));
        //verifyNoMoreInteractions(accountRepository);

    }

    @Test
    public void InvalidateAccountById_NegativeAvailableAmmount_ThrowsServerErrorException() throws Exception {

        // Given these instructions
        Mono<Account> expectedRepositoryResponse = buildExpectedResponse(NEGATIVE_AVAILABLE_AMMOUNT);
        when(accountRepository.findById(any(String.class))).thenReturn(expectedRepositoryResponse);

        // When this happens, then assert
        assertThrows(NullPointerException.class, () -> accountService.invalidate(expectedRepositoryResponse.block().getId(), PRINCIPAL));
    }

    @Test
    public void GetAmountByCVU_SuccessfullyTest() throws EmptyRequiredField {
        // Given these instructions
        Mono<Double> expectedRepositoryResponse = Mono.just(POSITIVE_AVAILABLE_AMMOUNT);
        when(accountRepository.getAmountByCVU(any(String.class))).thenReturn(expectedRepositoryResponse);

        // When this happens, then assert
        accountService.getAmountByCVU(CVU);
        verify(accountRepository, times(1)).getAmountByCVU(any(String.class));
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    public void UpdateAlias_SuccessfullyTest() throws EmptyRequiredField {
        // Given these instructions
        Mono<Account> expectedRepositoryResponse = buildExpectedResponse(POSITIVE_AVAILABLE_AMMOUNT);
        when(accountRepository.findById(any(String.class))).thenReturn(expectedRepositoryResponse);
        when(accountRepository.updateAlias(any(String.class), any(String.class))).thenReturn(expectedRepositoryResponse);

        // When this happens
        accountService.updateAlias(ACCOUNT_UUID, ALIAS, USER_UUID);

        // Then assert
        verify(accountRepository, times(1)).findById(any(String.class));
        verify(accountRepository, times(1)).updateAlias(any(String.class), any(String.class));
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    public void UpdateAmount_SuccessfullyTest() throws EmptyRequiredField {
        // Given these instructions
        Mono<Boolean> expectedRepositoryResponse = Mono.just(Boolean.TRUE);
        when(accountRepository.updateAmount(any(String.class), any(Double.class), any(TransactionType.class))).thenReturn(expectedRepositoryResponse);

        // When this happens
        accountService.updateAmount(ACCOUNT_UUID
                , AccountRequestDTO.builder().amount(POSITIVE_AVAILABLE_AMMOUNT).type(TransactionType.DEPOSIT).build());

        // Then assert
        verify(accountRepository, times(1)).updateAmount(any(String.class), any(Double.class), any(TransactionType.class));
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    public void GetAccountByAlias_SuccessfullyTest() throws EmptyRequiredField {
        // Given these instructions
        Mono<Account> expectedRepositoryResponse = buildExpectedResponse(POSITIVE_AVAILABLE_AMMOUNT);
        when(accountRepository.findByAlias(any(String.class))).thenReturn(expectedRepositoryResponse);

        // When this happens
        accountService.getByAlias(ALIAS);

        // Then assert
        verify(accountRepository, times(1)).findByAlias(any(String.class));
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    public void GetAccountByAliasNullParams_ThrowsEmptyRequiredFieldException() throws EmptyRequiredField {
        // When this happens, then assert
        assertThrows(EmptyRequiredField.class, () -> accountService.getByAlias(null), "Can't find account, id field is required");
        verifyNoInteractions(accountRepository);
    }

    @Test
    public void GetAccountByAliasEmptyParams_ThrowsEmptyRequiredFieldException() throws EmptyRequiredField {
        // When this happens, then assert
        assertThrows(EmptyRequiredField.class, () -> accountService.getByAlias(""), "Can't find account, id field is required");
        verifyNoInteractions(accountRepository);
    }

    @Test
    public void GetAccountByCVU_SuccessfullyTest() throws EmptyRequiredField {
        // Given these instructions
        Mono<Account> expectedRepositoryResponse = buildExpectedResponse(POSITIVE_AVAILABLE_AMMOUNT);
        when(accountRepository.findByCVU(any(String.class))).thenReturn(expectedRepositoryResponse);

        // When this happens
        accountService.getByCVU(ALIAS);

        // Then assert
        verify(accountRepository, times(1)).findByCVU(any(String.class));
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    public void GetAccountByCVUNullParams_ThrowsEmptyRequiredFieldException() throws EmptyRequiredField {
        // When this happens, then assert
        assertThrows(EmptyRequiredField.class, () -> accountService.getByCVU(null), "Can't find account, cvu field is required");
        verifyNoInteractions(accountRepository);
    }

    @Test
    public void GetAccountByCVUEmptyParams_ThrowsEmptyRequiredFieldException() throws EmptyRequiredField {
        // When this happens, then assert
        assertThrows(EmptyRequiredField.class, () -> accountService.getByCVU(""), "Can't find account, cvu field is required");
        verifyNoInteractions(accountRepository);
    }

    @Test
    public void ValidateAccountByUserId() {
        // Given these instructions
        Flux<Account> expectedResponse = buildExpectedResponse(NO_AVAILABLE_AMMOUNT).flux();
        Mockito.when(accountRepository.findAccountsByUserId(any(String.class))).thenReturn(expectedResponse);

        // When this happens
        accountService.validateAccountByUserId(ACCOUNT_UUID, USER_UUID);

        // Then assert
        verify(accountRepository, times(1)).findAccountsByUserId(any(String.class));
        verifyNoMoreInteractions(accountRepository);
    }

    private Mono<Account> buildExpectedResponse(Double availableAmmount) {
        AccountCA accountCA = AccountCA.builder()
                .alias(ALIAS)
                .cvu(CVU)
                .availableAmount(availableAmmount)
                .build();

        Account<?> account = Account.builder()
                .id(ACCOUNT_UUID)
                .type(AccountTypesEnum.CA)
                .userId(USER_UUID)
                .valid(IS_VALID_TRUE)
                .account(accountCA)
                .build();


        return Mono.just(account);
    }

    private Mono<Account> buildExpectedResponseCC(Double availableAmmount) {
        AccountCC accountCC = AccountCC.builder()
                .alias(ALIAS)
                .cvu(CVU)
                .availableAmount(availableAmmount)
                .build();

        Account<?> account = Account.builder()
                .id(ACCOUNT_UUID)
                .type(AccountTypesEnum.CC)
                .userId(USER_UUID)
                .valid(IS_VALID_TRUE)
                .account(accountCC)
                .build();


        return Mono.just(account);
    }
}
