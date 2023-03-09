package helper;

import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import endpoints.transactions.TransactionsEndpoints;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.transactions.Transaction;
import model.transactions.dto.NewTransactionDTO;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import utils.ConfigManager;

import java.util.HashMap;
import java.util.List;

public class TransactionsServiceHelper {

    private static final String BASE_URL = ConfigManager.getInstance().getProperty("rest.application.base-url");

    public ObjectMapper objectMapper;

    public TransactionsServiceHelper() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.useRelaxedHTTPSValidation();
        objectMapper = new ObjectMapper();
    }

    public Transaction createTransaction(NewTransactionDTO transaction, String token) throws JsonProcessingException {

        Response response = RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(transaction)
                .post(TransactionsEndpoints.CREATE_TRANSACTION)
                .andReturn();

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        String actualJson = response.getBody().asString();
        Transaction actualResponse = objectMapper.readValue(actualJson, new TypeReference<>() {});

        return actualResponse;
    }

    public Transaction getById(String id, String token) throws JsonProcessingException {

        Response response = RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .pathParam("id", id)
                .get(TransactionsEndpoints.GET_BY_ID)
                .andReturn();

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        String actualJson = response.getBody().asString();
        Transaction actualResponse = objectMapper.readValue(actualJson, new TypeReference<>() {});

        return actualResponse;
    }

    public List<Transaction> getTransactionsByIdWithLimit(String userId, Integer limit, String token) throws JsonProcessingException {

        Response response = RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .pathParam("userId", userId)
                .pathParam("limit", limit)
                .get(TransactionsEndpoints.GET_BY_USER_ID_LIMIT)
                .andReturn();

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        String actualJson = response.getBody().asString();
        List<Transaction> actualResponse = objectMapper.readValue(actualJson, new TypeReference<>() {});

        return actualResponse;
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
