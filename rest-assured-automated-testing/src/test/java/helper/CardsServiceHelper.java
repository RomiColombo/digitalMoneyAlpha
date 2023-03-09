package helper;

import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import endpoints.cards.CardsEndpoints;

import model.cards.Card;

import utils.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import io.restassured.response.Response;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import java.util.HashMap;
import java.util.List;


public class CardsServiceHelper {
    private static final String BASE_URL = ConfigManager.getInstance().getProperty("rest.application.base-url");

    public ObjectMapper objectMapper;

    public CardsServiceHelper() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.useRelaxedHTTPSValidation();
        objectMapper = new ObjectMapper();
    }

    public Card saveCard(Card card, String token) throws JsonProcessingException {

        Response response = RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(card)
                .post(CardsEndpoints.SAVE_CARD)
                .andReturn();

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        String actualJson = response.getBody().asString();
        Card actualResponse = objectMapper.readValue(actualJson, new TypeReference<>() {});

        return actualResponse;
    }

    public Card findById(String cardId, String token) throws JsonProcessingException {

        Response response = RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .pathParam("cardId", cardId)
                .get(CardsEndpoints.FIND_BY_ID)
                .andReturn();

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        String actualJson = response.getBody().asString();
        Card actualResponse = objectMapper.readValue(actualJson, new TypeReference<>() {});

        return actualResponse;
    }

    public List<Card> getByAccountId(String accountId, String token) throws JsonProcessingException {

        Response response = RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .pathParam("accountId", accountId)
                .get(CardsEndpoints.GET_BY_ACCOUNT_ID)
                .andReturn();

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        String actualJson = response.getBody().asString();
        List<Card> actualResponse = objectMapper.readValue(actualJson, new TypeReference<>() {});

        return actualResponse;
    }

    public Integer deleteById(String id,String token){

        Response response = RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .pathParam("id", id)
                .delete(CardsEndpoints.DELETE_BY_ID)
                .andReturn();

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        return response.getStatusCode();
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
