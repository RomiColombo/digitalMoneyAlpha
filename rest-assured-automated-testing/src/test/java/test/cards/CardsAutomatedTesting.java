package test.cards;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import helper.CardsServiceHelper;
import helper.Oauth2Helper;
import helper.UsersServiceHelper;
import model.cards.Card;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CardsAutomatedTesting {


    private static CardsServiceHelper cardsServiceHelper;
    private static UsersServiceHelper usersServiceHelper;
    private static ExtentReports extent;

    private static String ID;
    private static final String ACCOUNT_ID = "636d4fd3ada9225dc7b74806";
    private static final String USER_ID = "920e745e-913e-4520-a221-2f99b4736d83";
    private static String code = "";
    private static String token = "";

    private static String client_id = "gateway";
    private static String client_secret = "Wq17Q5I66wqedifVMqjHGlo9L9uRu5aZ";

    private static String username = "admin@test.com";
    private static String password = "1234";
    private static String redirect_uri = "http://localhost:8080/login/oauth2/code/gateway";


    @BeforeAll
    static void initTest() {

        cardsServiceHelper = new CardsServiceHelper();

        usersServiceHelper = new UsersServiceHelper();

        extent = new ExtentReports();

        extent.attachReporter(cardsServiceHelper.getReporter(CardsAutomatedTesting.class.getSimpleName()));

        log.info("The test suite was initialized");

    }

    @BeforeAll
    public static void getTokenTest() throws Exception {
        code = new Oauth2Helper().getOauthToken(username, password, client_id, redirect_uri);
        token = given().contentType(ContentType.URLENC)
                .urlEncodingEnabled(true)
                .formParam("grant_type", "authorization_code")
                .formParam("code", code)
                .formParam("redirect_uri", redirect_uri)
                .formParam("client_id", client_id)
                .formParam("client_secret", client_secret).when()
                .post("http://keycloak:8080/realms/digitalMoneyHouse/protocol/openid-connect/token").then().assertThat().statusCode(HttpStatus.SC_OK).extract()
                .jsonPath().get("access_token");
    }


    @Test
    @Order(1)
    @DisplayName("Get cards by account_id")
    void getByAccountIdTest() throws JsonProcessingException {

        ExtentTest test = extent.createTest("Get a list of cards");

        test.info("Starting test");

        List<Card> cardList = cardsServiceHelper.getByAccountId(ACCOUNT_ID, token);

        Assertions.assertNotNull(cardList, "The list is null");
        Assertions.assertNotNull(cardList.get(0).getFullName(), "The name is null");

        test.pass("Name: " + cardList.get(0).getFullName() +
                " - Number: " + cardList.get(0).getCardNumber() +
                " - Expiration date: " + cardList.get(0).getExpirationDate() +
                " - Code: " + cardList.get(0).getCode() +
                " - Company name: " + cardList.get(0).getCompanyName() +
                " - Account-id: " + cardList.get(0).getAccountId());
    }


    @Test
    @Order(2)
    @DisplayName("Create card")
    void createACardTest() throws JsonProcessingException {

        ExtentTest test = extent.createTest("Create a card");

        test.info("Starting test");
        //Integer random = (int) (Math.random() * 1000000);

        Card card = cardsServiceHelper.saveCard(Card.builder()
                .fullName("Test")
                .accountId(ACCOUNT_ID)
                .cardNumber(524477767012345L)
                .expirationDate("08/25")
                .code(123)
                .userId(USER_ID)
                .build(), token);

        ID = card.getId();

        Assertions.assertNotNull(card.getFullName(), "The name is null");
        Assertions.assertFalse(card.getFullName().isEmpty(), "The name is empty");

        test.pass("Name: " + card.getFullName() +
                " - Number: " + card.getCardNumber() +
                " - Expiration date: " + card.getExpirationDate() +
                " - Code: " + card.getCode() +
                " - Company name: " + card.getCompanyName() +
                " - Account-id: " + card.getAccountId());

    }


   @Test
    @Order(3)
    @DisplayName("Get card by ID")
    void getCardByIdTest() throws JsonProcessingException {
        ExtentTest test = extent.createTest("Get card by ID");
        test.info("Starting test");

        Card card = cardsServiceHelper.findById(ID, token);

        Assertions.assertNotNull(card.getFullName(), "The name is null");
        Assertions.assertFalse(card.getFullName().isEmpty(), "The name is empty");

        test.pass("Name: " + card.getFullName() +
                " - Number: " + card.getCardNumber() +
                " - Expiration date: " + card.getExpirationDate() +
                " - Code: " + card.getCode() +
                " - Company name: " + card.getCompanyName() +
                " - Account-id: " + card.getAccountId());
    }

    @Test
    @Order(4)
    @DisplayName("Delete card by id")
    void deleteCardTest() {

        ExtentTest test = extent.createTest("Delete card by id");
        test.info("Starting test");

        cardsServiceHelper.deleteById(ID, token);

    }

    @AfterAll
    public static void endTest() {

        usersServiceHelper.logout(token);
        extent.flush();
        log.info("Test suite finished");
    }

}
