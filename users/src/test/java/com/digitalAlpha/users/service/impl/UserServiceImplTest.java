package com.digitalAlpha.users.service.impl;

import com.digitalAlpha.users.exception.ResourceNotFoundException;
import com.digitalAlpha.users.model.UserKeycloak;
import com.digitalAlpha.users.model.dto.AccountDTO;
import com.digitalAlpha.users.model.dto.UserAccountDTO;
import com.digitalAlpha.users.model.dto.UserRegisterDTO;
import com.digitalAlpha.users.repository.IAccountsFeignRepository;
import com.digitalAlpha.users.repository.ITransfersFeignRepository;
import com.digitalAlpha.users.repository.implementation.KeycloakRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceImplTest {

    private static final String USER_UUID = UUID.randomUUID().toString();
    private static final String DNI = "12345678";
    private static final String EMAIL = "mail@alpha.com";
    private static final String FIRSTNAME = "Nombre";
    private static final String LASTNAME = "Apellido";
    private static final String PASSWORD = "Abc123";
    private static final String PHONE_NUMBER = "0123456789";

    @Mock
    private KeycloakRepository keycloakRepository;

    @Mock
    private IAccountsFeignRepository accountsFeignRepository;

    @Mock
    private ITransfersFeignRepository iTransfersFeignRepository;

    @InjectMocks
    private UserResourceService userResourceService;

    @Mock
    private ModelMapper modelMapper;
/*
    @Test
    public void CreateUser_SuccessfullyTest() throws Exception{

        // Given these instructions
        UserAccountDTO expectedServiceResponse = buildUserAccountDTO();
        UserKeycloak expectedRepositoryResponse = buildUserKeycloak();
        UserRegisterDTO newUser = buildUserRegisterDTO();

        Map<String,String> feignParameter = new HashMap<String, String>();
        feignParameter.put("userId",expectedRepositoryResponse.getId());

        Mockito.when(keycloakRepository.save(any(UserKeycloak.class))).thenReturn(expectedRepositoryResponse);
        Mockito.when(accountsFeignRepository.save(feignParameter)).thenReturn(new AccountDTO());
        Mockito.when(modelMapper.map(any(), any())).thenReturn(expectedServiceResponse);

        // When this happens
        UserAccountDTO actualResponse = userResourceService.saveUser(newUser);

        // Then assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getDni()).isEqualTo(expectedServiceResponse.getDni());
        assertThat(actualResponse.getEmail()).isEqualTo(expectedServiceResponse.getEmail());
        assertThat(actualResponse.getAccounts()).isEqualTo(expectedServiceResponse.getAccounts());
        assertThat(actualResponse.getFirstname()).isEqualTo(expectedServiceResponse.getFirstname());
        assertThat(actualResponse.getLastname()).isEqualTo(expectedServiceResponse.getLastname());
        assertThat(actualResponse.getPhone()).isEqualTo(expectedServiceResponse.getPhone());
        verify(keycloakRepository, times(1)).save(any(UserKeycloak.class));

    }

    @Test
    public void FindUserById_SuccessfullyTest() {

        // Given these instructions
        UserAccountDTO expectedServiceResponse = buildUserAccountDTO();
        UserKeycloak expectedRepositoryResponse = buildUserKeycloak();
        Mockito.when(keycloakRepository.findById(any(String.class))).thenReturn(expectedRepositoryResponse);
        Mockito.when(modelMapper.map(any(), any())).thenReturn(expectedServiceResponse);

        // When this happens
        UserAccountDTO actualResponse = userResourceService.getUser(USER_UUID);

        // Then assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getDni()).isEqualTo(expectedServiceResponse.getDni());
        assertThat(actualResponse.getEmail()).isEqualTo(expectedServiceResponse.getEmail());
        assertThat(actualResponse.getAccounts()).isEqualTo(expectedServiceResponse.getAccounts());
        assertThat(actualResponse.getFirstname()).isEqualTo(expectedServiceResponse.getFirstname());
        assertThat(actualResponse.getLastname()).isEqualTo(expectedServiceResponse.getLastname());
        assertThat(actualResponse.getPhone()).isEqualTo(expectedServiceResponse.getPhone());
        verify(keycloakRepository, times(1)).findById(any(String.class));

    }

    @Test
    public void InvalidateUserById_SuccessfullyTest() throws Exception {

        // Given these instructions
        UserKeycloak expectedRepositoryResponse = buildUserKeycloak();
        AccountDTO expectedFeignResponse = new AccountDTO();

        when(keycloakRepository.findById(any(String.class))).thenReturn(expectedRepositoryResponse);
        when(accountsFeignRepository.findAccountsByUserId(any(String.class))).thenReturn(List.of(expectedFeignResponse));
        when(accountsFeignRepository.invalidateAccount(any(String.class))).thenReturn("test");

        // When this happens
        userResourceService.invalidateUser(USER_UUID);

        // Then assert
        verify(keycloakRepository, times(1)).findById(any(String.class));
        verify(keycloakRepository, times(1)).invalidateUser(any(String.class));
        verifyNoMoreInteractions(keycloakRepository);

    }

    @Test
    public void InvalidateUserById_UserNotFound_ThrowsResourceNotFoundException() {

        // Given these instructions
        when(keycloakRepository.findById(any(String.class))).thenReturn(null);

        // When this happens, then assert
        assertThrows(ResourceNotFoundException.class, () -> userResourceService.invalidateUser(USER_UUID), "User with ID " + USER_UUID + " doesn't exist");
        verify(keycloakRepository, times(1)).findById(any(String.class));

    }

    @Test
    public void LogoutById_SuccessfullyTest() {

        // Given these instructions
        doNothing().when(keycloakRepository).logout(any(String.class));

        // When this happens
        userResourceService.logout(USER_UUID);

        // Then assert
        verify(keycloakRepository, times(1)).logout(any(String.class));

    }

    private UserAccountDTO buildUserAccountDTO() {
        return UserAccountDTO.builder()
                .firstname(FIRSTNAME)
                .lastname(LASTNAME)
                .dni(DNI)
                .email(EMAIL)
                .phone(PHONE_NUMBER)
                .accounts(List.of(AccountDTO.builder().build()))
                .build();
    }

    private UserRegisterDTO buildUserRegisterDTO() {
        return UserRegisterDTO.builder()
                .email(EMAIL)
                .firstname(FIRSTNAME)
                .lastname(LASTNAME)
                .dni(DNI)
                .phone(PHONE_NUMBER)
                .password(PASSWORD)
                .build();
    }

    private UserKeycloak buildUserKeycloak() {
        return UserKeycloak.builder()
                .id(USER_UUID)
                .firstname(FIRSTNAME)
                .lastname(LASTNAME)
                .dni(DNI)
                .phone(PHONE_NUMBER)
                .username(EMAIL)
                .password(PASSWORD)
                .email(EMAIL)
                .build();
    }
*/
}

