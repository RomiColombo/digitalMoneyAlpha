package test.transactions;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import helper.Oauth2Helper;
import helper.TransactionsServiceHelper;
import helper.UsersServiceHelper;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import model.transactions.Transaction;
import model.transactions.dto.NewTransactionDTO;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionsAutomatedTesting {

    private static TransactionsServiceHelper transactionsServiceHelper;
    private static UsersServiceHelper usersServiceHelper;
    private static ExtentReports extent;

    private static String ID;
    private static String code = "";
    private static String token = "";

    private static String client_id = "gateway";
    private static String client_secret = "Wq17Q5I66wqedifVMqjHGlo9L9uRu5aZ";
    private static String username = "admin@test.com";
    private static String password = "1234";
    private static String redirect_uri = "http://localhost:8080/login/oauth2/code/gateway";

    private static final String USER_ID = "920e745e-913e-4520-a221-2f99b4736d83";

    @BeforeAll
    static void initTest() {

        transactionsServiceHelper = new TransactionsServiceHelper();

        usersServiceHelper = new UsersServiceHelper();

        extent = new ExtentReports();

        extent.attachReporter(transactionsServiceHelper.getReporter(TransactionsAutomatedTesting.class.getSimpleName()));

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
    @DisplayName("Create transaction")
    void createATransactionTest() throws JsonProcessingException {

        ExtentTest test = extent.createTest("Create a transaction");

        test.info("Starting test");
        NewTransactionDTO newTransaction = new NewTransactionDTO("incoming","920e745e-913e-4520-a221-2f99b4736d83", "636d4fd3ada9225dc7b74806","63777cd538b9e856f995ee18",200.00,"un test");

        Transaction transaction = transactionsServiceHelper.createTransaction(newTransaction, token);

        ID = transaction.getId();

        Assertions.assertEquals(transaction.getAccountFrom(), "636d4fd3ada9225dc7b74806");
        Assertions.assertEquals(transaction.getAccountTo(), "63777cd538b9e856f995ee18");

        test.pass("Id: " + transaction.getId() +
                " - Origin: " + transaction.getAccountFrom() +
                " - Destination: " + transaction.getAccountTo() +
                " - Type: " + transaction.getType() +
                " - Description: " + transaction.getDescription() +
                " - Date: " + transaction.getTime().toString()
        );
    }

    @Test
    @Order(2)
    @DisplayName("Get transaction by ID")
    void getTransactionByIdTest() throws JsonProcessingException {
        ExtentTest test = extent.createTest("Get transaction by ID");
        test.info("Starting test");

        Transaction transaction = transactionsServiceHelper.getById(ID, token);

        Assertions.assertNotNull(transaction.getId(), "The Id is null");
        Assertions.assertFalse(transaction.getUser().isEmpty(), "The name is empty");

        test.pass("Id: " + transaction.getId() +
                " - Origin: " + transaction.getAccountFrom() +
                " - Destination: " + transaction.getAccountTo() +
                " - Type: " + transaction.getType() +
                " - Description: " + transaction.getDescription() +
                " - Date: " + transaction.getTime().toString()
        );
    }

    @Test
    @Order(3)
    @DisplayName("Get transaction by ID with limit")
    void getTransactionByIdWithLimitTest() throws JsonProcessingException {
        ExtentTest test = extent.createTest("Get transaction by ID");
        test.info("Starting test");

        int limit = 5;

        List<Transaction> transactions = transactionsServiceHelper.getTransactionsByIdWithLimit(USER_ID, limit, token);

        Assertions.assertNotNull(transactions, "The list is null");
        Assertions.assertNotNull(transactions.get(0).getStatus(), "The status is null");

        test.pass("Id: " + transactions.get(0).getId() +
                " - Origin: " + transactions.get(0).getAccountFrom() +
                " - Destination: " + transactions.get(0).getAccountTo() +
                " - Type: " + transactions.get(0).getType() +
                " - Description: " + transactions.get(0).getDescription() +
                " - Date: " + transactions.get(0).getTime().toString()
        );
    }


    @AfterAll
    public static void endTest() {

        usersServiceHelper.logout(token);
        extent.flush();
        log.info("Test suite finished");
    }
}
