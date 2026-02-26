package com.openapistore.tests.auth;

import com.openapistore.models.auth.LoginRequest;
import com.openapistore.models.auth.LoginResponse;
import com.openapistore.tests.base.BaseTest;
import com.openapistore.utils.StatusCodeAsserter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class AuthTests extends BaseTest {

    @Test(description = "AUTH_LOGIN_001 - Login with valid credentials and receive JWT token")
    public void AUTH_LOGIN_001_validLogin() {
        LoginRequest loginRequest = new LoginRequest("mor_2314", "83r5^_");

        Response response = given()
                .spec(requestSpec)
                .body(loginRequest)
                .when()
                .post("/auth/login");

        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("schemas/auth/loginResponse.schema.json"));

        LoginResponse loginResponse = response.as(LoginResponse.class);
        Assert.assertNotNull(loginResponse.getToken(), "token should be present");
        Assert.assertFalse(loginResponse.getToken().isBlank(), "token should be a non-empty string");
    }

    @Test(description = "AUTH_LOGIN_002 - Login fails with wrong password (may return 401 or 400)")
    public void AUTH_LOGIN_002_wrongPassword() {
        LoginRequest loginRequest = new LoginRequest("mor_2314", "wrong_password");

        Response response = given()
                .spec(requestSpec)
                .body(loginRequest)
                .when()
                .post("/auth/login");

        // FakeStore may return 400 instead of 401.
        StatusCodeAsserter.assertStatus(response, 401, 400);
        Reporter.log("Response body (if any): " + response.asString(), true);
    }

    @Test(description = "AUTH_LOGIN_003 - Login fails when username is missing (may return 400 or 422)")
    public void AUTH_LOGIN_003_missingUsername() {
        Map<String, Object> body = new HashMap<>();
        body.put("password", "83r5^_");

        Response response = given()
                .spec(requestSpec)
                .body(body)
                .when()
                .post("/auth/login");

        StatusCodeAsserter.assertStatus(response, 400, 422);
        Reporter.log("Response body (if any): " + response.asString(), true);
    }

    @Test(description = "AUTH_LOGIN_004 - Login fails when password is null")
    public void AUTH_LOGIN_004_passwordNull() {
        LoginRequest loginRequest = new LoginRequest("mor_2314", null);

        Response response = given()
                .spec(requestSpec)
                .body(loginRequest)
                .when()
                .post("/auth/login");

        StatusCodeAsserter.assertStatus(response, 400, 422);
        Reporter.log("Response body (if any): " + response.asString(), true);
    }

    @Test(description = "AUTH_LOGIN_005 - Login fails with malformed JSON payload")
    public void AUTH_LOGIN_005_malformedJson() {
        String rawBody = "{ \"username\": \"mor_2314\", \"password\": }";

        Response response = given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rawBody)
                .when()
                .post("/auth/login");

        // Most servers respond with 400 for invalid JSON.
        StatusCodeAsserter.assertStatus(response, 400);
        Reporter.log("Response body (if any): " + response.asString(), true);
    }
}
