package helper;

import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import endpoints.accounts.AccountsEndpoints;
import model.accounts.Account;
import model.accounts.dto.AccountEntryDTO;

import utils.ConfigManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.List;


public class AccountsServiceHelper {

    private static final String BASE_URL = ConfigManager.getInstance().getProperty("rest.application.base-url");

    public ObjectMapper objectMapper;

    public AccountsServiceHelper() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.useRelaxedHTTPSValidation();
        objectMapper = new ObjectMapper();
    }

    public Account findAccountById(String id, String token) throws JsonProcessingException {

        Response response = RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .pathParam("id", id)
                .get(AccountsEndpoints.GET_BY_ID)
                .andReturn();

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        String actualJson = response.getBody().asString();
        Account actualResponse = objectMapper.readValue(actualJson, new TypeReference<>() {});

        return actualResponse;
    }

    public List<Account> findAccountsByUserId(String userId, String token) throws JsonProcessingException {

        Response response = RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .header("Authorization", "Bearer " + token)
                .get(AccountsEndpoints.GET_BY_USER_ID)
                .andReturn();

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        String actualJson = response.getBody().asString();
       List<Account> actualResponse = objectMapper.readValue(actualJson, new TypeReference<>() {});

        return actualResponse;
    }

    public Account findAccountAlias (String alias, String token) throws JsonProcessingException {

        Response response = RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .pathParam("alias", alias)
                .get(AccountsEndpoints.GET_BY_ALIAS)
                .andReturn();

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        String actualJson = response.getBody().asString();
        Account actualResponse = objectMapper.readValue(actualJson, new TypeReference<>() {});

        return actualResponse;
    }


    public Account createAccount (String userId, String type, String token) throws JsonProcessingException {

        AccountEntryDTO accountEntryDTO = new AccountEntryDTO(userId, type);

        Response response = RestAssured.given()
                .log().all()
                .contentType(ContentType.URLENC)
                .header("Authorization", "Bearer " + token)
                .formParam("userId", userId)
                .formParam("type", type)
                .post(AccountsEndpoints.CREATE_ACCOUNT)
                .andReturn();

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        String actualJson = response.getBody().asString();
        Account actualResponse = objectMapper.readValue(actualJson, new TypeReference<>() {});

        return actualResponse;

    }

    public Response updateAlias (String id, String alias, String token) {

        return RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .pathParam("id", id)
                .body(alias)
                .patch(AccountsEndpoints.UPDATE_ALIAS)
                .andReturn();
    }

    public ExtentSparkReporter getReporter(String testSuiteName) {

        String testSuiteLower = testSuiteName.replaceAll("\\s+", "").toLowerCase();
        HashMap<String, ExtentSparkReporter> flyweightReporter = new HashMap<>();

        if(!flyweightReporter.containsKey(testSuiteLower)) {
            flyweightReporter.put(testSuiteLower, new ExtentSparkReporter("target/reports/" + testSuiteName + "Report.html"));
        }

        return flyweightReporter.get(testSuiteLower);
    }

}