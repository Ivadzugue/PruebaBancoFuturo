package apiTesting;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class usersTokenTest {
    Cookie userAccessCookie = new Cookie.Builder("access-token", "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9." +
            "IsInVzZXJuYW1lIjoiZGV0ZWN0LWFkbWluIn0.84FcphVQM2wA33-rL9ObAPG-cAyuhSnwCxOaL7q_k1nVAyQyVSG7whdhw" +
            "mCGJzFENPV-7kqmEMyvJo-MdVhvkw").build();

    Cookie refreshTokenCookie = new Cookie.Builder("","").build();
    Cookie userInfoCookie = new Cookie.Builder("","").build();

    @Before
    public void setup() {

        RestAssured.baseURI = "http://bancofuturo:9999/authentication-api";

        HashMap<String, String> postContent = new HashMap<String, String>();
        postContent.put("grantType", "PASSWORD");
        postContent.put("username", "detect-admin");
        postContent.put("password", "admin");
        Response response = given()
                .cookie(userAccessCookie)
                .contentType(ContentType.JSON)
                .with()
                .body(postContent)
                .when()
                .post("/token");


        String accessTokenCookieContent = response.getBody().path("accessToken").toString();
        String refreshTokenCookieContent =  response.getBody().path("refreshToken").toString();
        String userInfoCookieContent  = response.getCookie("user-info");

        this.userInfoCookie = new Cookie.Builder("user-info", userInfoCookieContent).build();
        this.userAccessCookie = new Cookie.Builder("access-token",  accessTokenCookieContent).build();
        this.refreshTokenCookie = new Cookie.Builder("refresh-token",refreshTokenCookieContent).build();
    }

    //1.Authentication password, this scenario validates the correct login of the user, response code 200
    @Test
    public void authenPass() {
        HashMap<String, String> postContent = new HashMap<String, String>();
        postContent.put("grantType", "PASSWORD");
        postContent.put("username", "user1");
        postContent.put("password", "UserPassword101*");
        given().
                cookie(userAccessCookie)
                .contentType(ContentType.JSON)
                .with()
                .body(postContent)
                .when()
                .post("/token")
                .then()
                .statusCode(200)
        ;
    }

    //2 The authenticated password user was not found, this scenario validates that it is not possible to log in, because the user does not exist
    @Test
    public void authPassUserNotFound() throws Exception {
        HashMap<String, String> postContent = new HashMap<String, String>();
        postContent.put("grantType", "PASSWORD");
        postContent.put("username", "user2");
        postContent.put("password", "PassworddoesnotExist101*");
        given()
                .cookie(userAccessCookie)
                .contentType(ContentType.JSON)
                .with()
                .body(postContent)
                .when()
                .post("/token")
                .then()
                .statusCode(400)
                .body(
                        "errorType", equalTo("INVALID_CREDENTIALS"),
                        "message", equalTo("Provided username or password is incorrect"))
        ;
    }

    //3.This scenario, create new user.
    @Test
    public void newUser() {
        HashMap<String, String> postContent = new HashMap<String, String>();
        postContent.put("username", "newUser");
        postContent.put("name", "new test");
        postContent.put("email", "mail@bancofuturo.com");
        postContent.put("password", "Password101*");
        postContent.put("confirmpassword", "Password101*");
        given()
                .cookie(userAccessCookie)
                .contentType(ContentType.JSON).
                with()
                .body(postContent).
                when().
                post("/users").
                then().statusCode(201)
                .body(
                        "username", equalTo("newUser"),
                        "name", equalTo("new test"),
                        "email", equalTo("mail@appgate.com"),
                        "language", equalTo("en"))
        ;
    }
}