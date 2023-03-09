package helper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class Oauth2Helper {


    /***
     * --------------------- Design ------------------------
     * 1. ChromeWebDriver -> The responsibility is to create the instance of chrome browse
     * 2. OAuthHelper ->
     * 	a. Call the ChromeWebDriver to get the instance of chrome browser.
     * 	b. Send the request to authorize end point and extract the code.
     * 3. TestGetListofFile
     * */

    private WebDriver driver;
    private WebDriverWait wait;
    private By login_email = By.id("username");
    private By login_password = By.id("password");
    private By submit = By.id("kc-login");

    public Oauth2Helper() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofMillis(1500));
    }

    private String getBaseUrl(String redirect_uri, String client_id) {
        String url = String.format(
              "http://keycloak:8080/realms/digitalMoneyHouse/protocol/openid-connect/auth?response_type=code&redirect_uri="+redirect_uri+"&client_id="+client_id+"&scope=openid");
        return url;
    }


    private void login(String username, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(submit));
        driver.findElement(login_email).sendKeys(username);
        driver.findElement(login_password).sendKeys(password);
        driver.findElement(submit).click();
        wait.until(ExpectedConditions.urlContains("&code="));

    }

    private void setup() {
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.manage().window().maximize();

    }

    public String getOauthToken(String username, String password, String client_id, String redirect_uri) throws Exception {
        String currUrl;
        try {
            setup();
            String baseUrl = getBaseUrl(redirect_uri, client_id);
            driver.navigate().to(baseUrl);
            login(username, password);
            currUrl = driver.getCurrentUrl();
        } finally {
            if (driver != null)
                driver.quit();
        }
        return parseToken(currUrl);
    }

    private String parseToken(String code) {
        String authcode = "";
        if (code.contains("&code=")) {
            authcode = code.split("&code=")[1];
        }
        return authcode;
    }
}
