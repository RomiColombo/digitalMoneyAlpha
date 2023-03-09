package test.accounts;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import helper.AccountsServiceHelper;
import helper.Oauth2Helper;
import helper.UsersServiceHelper;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import model.accounts.Account;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountsAutomatedTesting {

    private static AccountsServiceHelper accountsServiceHelper;
    private static UsersServiceHelper usersServiceHelper;
    private static ExtentReports extent;

    private static String code = "";
    private static String token = "";

    private static String client_id = "gateway";
    private static String client_secret = "Wq17Q5I66wqedifVMqjHGlo9L9uRu5aZ";

    private static String username = "admin@test.com";
    private static String password = "1234";
    private static String redirect_uri = "http://localhost:8080/login/oauth2/code/gateway";

    private static final String ACCOUNT_ID = "636d4fd3ada9225dc7b74806";
    private static final String ACCOUNT_ALIAS = "un.alias.random";
    private static final String ACCOUNT_NEW_ALIAS = "un.nuevo.alias";
    private static final String USER_ID = "920e745e-913e-4520-a221-2f99b4736d83";


    @BeforeAll
    static void initTest() {

        accountsServiceHelper = new AccountsServiceHelper();

        usersServiceHelper = new UsersServiceHelper();

        extent = new ExtentReports();

        extent.attachReporter(accountsServiceHelper.getReporter(AccountsAutomatedTesting.class.getSimpleName()));

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
    @DisplayName("Get account by id")
    void getByIdTest() throws JsonProcessingException {

        ExtentTest test = extent.createTest("Get a account");

        test.info("Starting test");

        Account account = accountsServiceHelper.findAccountById(ACCOUNT_ID, token);

        Assertions.assertNotNull(account.getId(), "The id is null");
        Assertions.assertNotNull(account.getType(), "The type is null");

        test.pass("Account type: " + account.getType() +
                " - User-id: " + account.getUserId() +
                " - is Valid: " + account.isValid());
    }

    @Test
    @Order(2)
    @DisplayName("Get account by alias")
    void getByAlias() throws JsonProcessingException {

        ExtentTest test = extent.createTest("Get a account by alias");

        test.info("Starting test");

        Account account = accountsServiceHelper.findAccountAlias(ACCOUNT_ALIAS, token);

        Assertions.assertNotNull(account.getId(), "The id is null");
        //Assertions.assertNotNull(account.getType(), "The type is null");

        test.pass("Account type: " + account.getType() +
                " - User-id: " + account.getUserId() +
                " - is Valid: " + account.isValid());
    }

    @Test
    @Order(3)
    @DisplayName("Get accounts by userId")
    void getByUserId() throws JsonProcessingException {

        ExtentTest test = extent.createTest("Get a list of accounts");

        test.info("Starting test");

        List<Account> accountList = accountsServiceHelper.findAccountsByUserId(USER_ID, token);

        Assertions.assertNotNull(accountList, "The list is null");
        Assertions.assertNotNull(accountList.get(0).getType(), "The type is null");

        test.pass("Account type: " + accountList.get(0).getType() +
                " - User-id: " + accountList.get(0).getUserId() +
                " - is Valid: " + accountList.get(0).isValid());
    }


    @Test
    @Order(4)
    @DisplayName("Create account")
    void createAccountTest() throws JsonProcessingException {

        ExtentTest test = extent.createTest("Create a card");

        test.info("Starting test");

        Account account = accountsServiceHelper.createAccount(USER_ID, "CA", token);

        Assertions.assertNotNull(account.getId(), "The id is null");
        Assertions.assertNotNull(account.getType(), "The type is null");

        test.pass("Account type: " + account.getType() +
                " - User-id: " + account.getUserId() +
                " - is Valid: " + account.isValid());
    }


    @Test
    @Order(5)
    @DisplayName("Update alias")
    void updateAliasTest() throws JsonProcessingException {
        ExtentTest test = extent.createTest("Get card by ID");
        test.info("Starting test");

        accountsServiceHelper.updateAlias(ACCOUNT_ID, ACCOUNT_NEW_ALIAS, token);
    }

    @AfterAll
    public static void endTest() {

        usersServiceHelper.logout(token);
        extent.flush();
        log.info("Test suite finished");
    }
}
