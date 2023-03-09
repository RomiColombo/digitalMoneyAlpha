package com.digitalAlpha.users.controller;

import com.digitalAlpha.users.model.UserKeycloak;
import com.digitalAlpha.users.model.dto.AccountDTO;
import com.digitalAlpha.users.model.dto.UserAccountDTO;
import com.digitalAlpha.users.model.dto.UserRegisterDTO;
import com.digitalAlpha.users.service.impl.UserResourceService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {

    private static final String USER_UUID = UUID.randomUUID().toString();
    private static final String DNI = "12345678";
    private static final String EMAIL = "mail@alpha.com";
    private static final String FIRSTNAME = "Nombre";
    private static final String LASTNAME = "Apellido";
    private static final String PASSWORD = "Abc123";
    private static final String PHONE_NUMBER = "0123456789";
    private static final String BASE_URI = "/users";

    private static final Principal PRINCIPAL = () -> USER_UUID;

    @MockBean
    private UserResourceService userResourceService;

    @MockBean
    private Keycloak keycloakClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void abc() {

    }
/*
    @Test
    public void CreateUser_SuccessfullyTest() throws Exception{

        // Given these instructions
        UserAccountDTO expectedServiceResponse = buildExpectedResponse();
        String expectedJson = objectMapper.writeValueAsString(expectedServiceResponse);

        UserRegisterDTO newUser = UserRegisterDTO.builder()
                .email(EMAIL)
                .firstname(FIRSTNAME)
                .lastname(LASTNAME)
                .dni(DNI)
                .phone(PHONE_NUMBER)
                .password(PASSWORD)
                .build();
        String newUserJson = objectMapper.writeValueAsString(newUser);

        when(userResourceService.saveUser(any(UserRegisterDTO.class))).thenReturn(expectedServiceResponse);

        // When this happens
        MvcResult result =
                mockMvc
                        .perform(
                                post(BASE_URI)
                                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                        .param("userRegisterDTO", newUserJson))
                        .andExpect(status().isCreated())
                        .andReturn();

        String actualJson = result.getResponse().getContentAsString();
        UserAccountDTO actualResponse = objectMapper.readValue(actualJson, new TypeReference<>() {});

        // Then assert
        assertThat(actualJson).isNotNull();
        assertThat(actualJson).isEqualTo(expectedJson);

        assertThat(actualResponse.getPhone()).isEqualTo(newUser.getPhone());
        assertThat(actualResponse.getLastname()).isEqualTo(newUser.getLastname());
        assertThat(actualResponse.getDni()).isEqualTo(newUser.getDni());
        assertThat(actualResponse.getEmail()).isEqualTo(newUser.getEmail());
        assertThat(actualResponse.getFirstname()).isEqualTo(newUser.getFirstname());
        assertThat(actualResponse.getAccounts()).isNotNull();
        assertThat(actualResponse.getTransactions()).isNull();

        verify(userResourceService, times(1)).saveUser(any(UserRegisterDTO.class));

    }

    @Test
    public void FindUserById_SuccessfullyTest() throws Exception {

        // Given this instructions
        UserAccountDTO expectedResponse = buildExpectedResponse();
        String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        when(userResourceService.getUser(any(String.class))).thenReturn(expectedResponse);

        // When this happens
        MvcResult result =
                mockMvc
                        .perform(
                                get(BASE_URI + "/{id}", USER_UUID))
                        .andExpect(status().isOk())
                        .andReturn();

        String actualJson = result.getResponse().getContentAsString();
        UserAccountDTO actualResponse = objectMapper.readValue(actualJson, new TypeReference<>() {});


        // Then assert
        assertThat(actualJson).isNotNull();
        assertThat(actualJson).isEqualTo(expectedJson);

        assertThat(actualResponse.getPhone()).isEqualTo(expectedResponse.getPhone());
        assertThat(actualResponse.getLastname()).isEqualTo(expectedResponse.getLastname());
        assertThat(actualResponse.getDni()).isEqualTo(expectedResponse.getDni());
        assertThat(actualResponse.getEmail()).isEqualTo(expectedResponse.getEmail());
        assertThat(actualResponse.getFirstname()).isEqualTo(expectedResponse.getFirstname());
        assertThat(actualResponse.getAccounts()).isNotNull();
        assertThat(actualResponse.getTransactions()).isNull();

        verify(userResourceService, times(1)).getUser(USER_UUID);

    }

    @Test
    public void DeleteUserById_SuccessfullyTest() throws Exception {

        //Given these instructions
        doNothing().when(userResourceService).invalidateUser(any(String.class));

        // When this happens
        MvcResult result =
                mockMvc
                        .perform(
                                delete(BASE_URI + "/{id}", USER_UUID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn();

        String actualJson = result.getResponse().getContentAsString();

        // Then assert
        assertThat(actualJson).isNotNull();
        assertThat(actualJson).isEqualTo("The user with id " + USER_UUID + "was successfully deleted");
        verify(userResourceService, times(1)).invalidateUser(USER_UUID);

    }

    @Test
    public void ModifyUserById_SuccessfullyTest() throws Exception {

        // Given these instructions
        UserAccountDTO expectedResponse = buildExpectedResponse();
        expectedResponse.setFirstname("new name");

        String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        when(userResourceService.modifyUser(any(UserKeycloak.class), any(String.class))).thenReturn(expectedResponse);

        UserKeycloak userKeycloak = buildUserKeycloak();

        // When this happens
        MvcResult result =
                mockMvc
                        .perform(
                                patch(BASE_URI + "/{id}", USER_UUID)
                                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                        .requestAttr("userKeycloak", userKeycloak))
                        .andExpect(status().isOk())
                        .andReturn();

        String actualJson = result.getResponse().getContentAsString();
        UserAccountDTO actualResponse = objectMapper.readValue(actualJson, new TypeReference<>() {});

        // Then assert
        assertThat(actualJson).isNotNull();
        assertThat(actualJson).isEqualTo(expectedJson);

        assertThat(actualResponse.getPhone()).isEqualTo(expectedResponse.getPhone());
        assertThat(actualResponse.getLastname()).isEqualTo(expectedResponse.getLastname());
        assertThat(actualResponse.getDni()).isEqualTo(expectedResponse.getDni());
        assertThat(actualResponse.getEmail()).isEqualTo(expectedResponse.getEmail());
        assertThat(actualResponse.getFirstname()).isEqualTo(expectedResponse.getFirstname());
        assertThat(actualResponse.getAccounts()).isNotNull();
        assertThat(actualResponse.getTransactions()).isNull();

    }


    @Test
    public void LogoutUser_SuccessfullyTest() throws Exception {

        // Given these instructions
        when(keycloakClient.realm(any(String.class)).users().get(any(String.class))).thenReturn(USER_UUID);
        //doNothing().when(keycloakClient).realm(any(String.class)).users().get(any(String.class)).logout();

        // When this happens
        MvcResult result =
                mockMvc
                        .perform(
                                post(BASE_URI + "/logout")
                                        .principal(PRINCIPAL))
                        .andExpect(status().isOk())
                        .andReturn();

        String actualJson = result.getResponse().getContentAsString();

        // Then assert
        assertThat(actualJson).isEqualTo("");
        verify(keycloakClient, times(1)).realm(any(String.class)).users().get(any(String.class)).logout();

    }


    private UserAccountDTO buildExpectedResponse() {
        return UserAccountDTO.builder()
                .firstname(FIRSTNAME)
                .lastname(LASTNAME)
                .dni(DNI)
                .email(EMAIL)
                .phone(PHONE_NUMBER)
                .accounts(List.of(AccountDTO.builder().build()))
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

