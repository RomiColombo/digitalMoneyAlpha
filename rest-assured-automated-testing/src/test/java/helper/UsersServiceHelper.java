package helper;

import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.fasterxml.jackson.core.type.TypeReference;
import endpoints.users.UsersEndpoints;

import model.users.TokenRequest;
import model.users.User;
import model.users.dto.UserResponseDTO;
import utils.ConfigManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;

public class UsersServiceHelper {

    private static final String BASE_URL = ConfigManager.getInstance().getProperty("rest.application.base-url");

    public ObjectMapper objectMapper;
    private Response response;

    public UsersServiceHelper() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.useRelaxedHTTPSValidation();
        objectMapper = new ObjectMapper();
    }

    public UserResponseDTO getById(String id, String token) throws JsonProcessingException {

        Response response = RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .pathParam("id", id)
                .get(UsersEndpoints.GET_BY_ID)
                .andReturn();

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        String actualJson = response.getBody().asString();
        UserResponseDTO actualResponse = objectMapper.readValue(actualJson, new TypeReference<>() {});

        return actualResponse;

    }

    public Map<String, Object> introspectToken(String token) {

        Response response = RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .get(UsersEndpoints.TOKEN_INTROSPECTION)
                .andReturn();

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        return response.as(HashMap.class);

    }

    public UserResponseDTO save(User user) throws JsonProcessingException {

        Response response = RestAssured.given()
                .log().all()
                .contentType(ContentType.URLENC)
                .formParam("email", user.getEmail())
                .formParam("firstname", user.getFirstname())
                .formParam("lastname", user.getLastname())
                .formParam("dni", user.getDni())
                .formParam("phone", user.getPhone())
                .formParam("password", user.getPassword())
               // .body(user)
                .post(UsersEndpoints.CREATE)
                .andReturn();

        Assertions.assertEquals(HttpStatus.SC_CREATED, response.getStatusCode());

        String actualJson = response.getBody().asString();
        UserResponseDTO actualResponse = objectMapper.readValue(actualJson, new TypeReference<>() {});

        return actualResponse;

    }

    public String login(TokenRequest tokenRequest) {

        response = RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .auth().basic("admin@test.com", "1234")
                .body(tokenRequest)
                .post(UsersEndpoints.TOKEN_URL)
                .andReturn();

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        return response.jsonPath().getString("access_token");

    }

    public Response logout(String token) {

        return RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .post(UsersEndpoints.LOGOUT)
                .andReturn();

    }

    public UserResponseDTO patchUpdate(String ID, String phone, String token) throws JsonProcessingException {

        Response response = RestAssured.given()
                .log().all()
                .contentType(ContentType.URLENC)
                .header("Authorization", "Bearer " + token)
                .formParam("phone", phone)
                //.body(user)
                .patch(UsersEndpoints.PATCH_UPDATE, ID)
                .andReturn();

        String actualJson = response.getBody().asString();
        UserResponseDTO actualResponse = objectMapper.readValue(actualJson, new TypeReference<>() {});

        return actualResponse;
    }

    public ExtentSparkReporter getReporter(String testSuiteName) {

        String testSuiteLower = testSuiteName.replaceAll("\\s+", "").toLowerCase();
        HashMap<String, ExtentSparkReporter> flyweightReporter = new HashMap<>();

        if (!flyweightReporter.containsKey(testSuiteLower)) {
            flyweightReporter.put(testSuiteLower, new ExtentSparkReporter("target/reports/" + testSuiteName + "Report.html"));
        }

        return flyweightReporter.get(testSuiteLower);

    }

    public Map<String, Object> getTokenInfo(String accessToken) throws JsonProcessingException {

        String[] split_string = accessToken.split("\\.");
        String base64EncodedBody = split_string[1];
        Base64 base64Url = new Base64(true);
        String body = new String(base64Url.decode(base64EncodedBody));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(body, Map.class);
    }

    public Integer deleteById(String id, String token){

        Response response = RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .pathParam("id", id)
                .delete(UsersEndpoints.DELETE_BY_ID)
                .andReturn();

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        return response.getStatusCode();
    }
}
