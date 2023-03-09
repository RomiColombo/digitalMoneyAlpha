package test.users;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import helper.Oauth2Helper;
import helper.UsersServiceHelper;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import model.users.User;
import model.users.dto.UserResponseDTO;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersAutomatedTesting {

    private static UsersServiceHelper usersServiceHelper;
    private static ExtentReports extent;
    private static String code = "";
    private static String token = "";

    private static String client_id = "gateway";
    private static String client_secret = "Wq17Q5I66wqedifVMqjHGlo9L9uRu5aZ";

    private static String username = "admin@test.com";
    private static String password = "1234";
    private static String redirect_uri = "http://localhost:8080/login/oauth2/code/gateway";

    //
    private static final User
            USER = new User("unmail99@test.com", "automation", "test", "123456712", "34567","4567");

    private static String NEW_USER_ID;
    private static final String USER_ID = "920e745e-913e-4520-a221-2f99b4736d83";


    @BeforeAll
    static void initTest() {


        usersServiceHelper = new UsersServiceHelper();

        extent = new ExtentReports();

        extent.attachReporter(usersServiceHelper.getReporter(UsersAutomatedTesting.class.getSimpleName()));

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
    @DisplayName("Create user")
    void registerTest() throws JsonProcessingException {

        ExtentTest test = extent.createTest("Register a user");

        test.info("Starting test");

        UserResponseDTO user = usersServiceHelper.save(USER);

        NEW_USER_ID = user.getAccounts().get(0).getUserId();

        Assertions.assertNotNull(user.getFirstname(), "The name is null");
        Assertions.assertNotNull(user.getPhone(), "The phone is null");

        test.pass("email: " + user.getEmail() +
                " - first name: " + user.getFirstname() +
                " - last name: " + user.getLastname() +
                " - dni: "+ user.getDni() +
                " - phone: "+user.getPhone());
    }

    @Test
    @Order(2)
    @DisplayName("Get user by id")
    void getByIdTest() throws JsonProcessingException {

        ExtentTest test = extent.createTest("Get a user");

        test.info("Starting test");

        UserResponseDTO user = usersServiceHelper.getById(USER_ID, token);

        Assertions.assertNotNull(user.getFirstname(), "The name is null");
        Assertions.assertNotNull(user.getPhone(), "The phone is null");

        test.pass("email: " + user.getEmail() +
                " - first name: " + user.getFirstname() +
                " - last name: " + user.getLastname() +
                " - dni: "+ user.getDni() +
                " - phone: "+user.getPhone());
    }

    @Test
    @Order(3)
    @DisplayName("Partial update")
    void updateUser() throws JsonProcessingException {

        ExtentTest test = extent.createTest("Update some fields of user");

        test.info("Starting test");

        UserResponseDTO user = usersServiceHelper.patchUpdate(USER_ID, "908754", token);

        Assertions.assertNotNull(user.getFirstname(), "The name is null");
        Assertions.assertNotNull(user.getPhone(), "The phone is null");

        test.pass("email: " + user.getEmail() +
                " - first name: " + user.getFirstname() +
                " - last name: " + user.getLastname() +
                " - dni: "+ user.getDni() +
                " - phone: "+user.getPhone());
    }

    @Test
    @Order(4)
    @DisplayName("Delete user")
    void getCardByIdTest() throws JsonProcessingException {
        ExtentTest test = extent.createTest("Delete user by id");
        test.info("Starting test");

        usersServiceHelper.deleteById(NEW_USER_ID, token);
    }

    @AfterAll
    public static void endTest() {

        usersServiceHelper.logout(token);
        extent.flush();
        log.info("Test suite finished");
    }
}
