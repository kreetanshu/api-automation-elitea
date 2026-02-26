package com.openapistore.tests.auth;

import com.openapistore.models.auth.AuthLoginRequest;
import com.openapistore.tests.BaseApiTest;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class AuthLoginTests extends BaseApiTest {

  private static final String TOKEN_REGEX = "^[A-Za-z0-9\\-_]+\\.[A-Za-z0-9\\-_]+\\.[A-Za-z0-9\\-_]+$";

  @Test(description = "AUTH_LOGIN_001 - Login with valid credentials and receive JWT token.")
  public void AUTH_LOGIN_001_validLogin_shouldReturnToken() {
    AuthLoginRequest body = new AuthLoginRequest("mor_2314", "83r5^_");

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .post("/auth/login")
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("token", allOf(not(isEmptyOrNullString()), matchesPattern(TOKEN_REGEX)))
        .body(matchesJsonSchemaInClasspath("schemas/auth_login_success_schema.json"));
  }

  @Test(description = "AUTH_LOGIN_002 - Login fails with invalid password.")
  public void AUTH_LOGIN_002_invalidPassword_shouldReturn401() {
    AuthLoginRequest body = new AuthLoginRequest("mor_2314", "wrong_password");

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .post("/auth/login")
      .then()
        .statusCode(401)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "AUTH_LOGIN_003 - Login fails when username is missing.")
  public void AUTH_LOGIN_003_missingUsername_shouldReturn400() {
    AuthLoginRequest body = new AuthLoginRequest(null, "83r5^_");

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .post("/auth/login")
      .then()
        .statusCode(400)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "AUTH_LOGIN_004 - Login fails when password is missing.")
  public void AUTH_LOGIN_004_missingPassword_shouldReturn400() {
    AuthLoginRequest body = new AuthLoginRequest("mor_2314", null);

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .post("/auth/login")
      .then()
        .statusCode(400)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "AUTH_LOGIN_005 - Login fails when request body types are invalid (non-string username/password).")
  public void AUTH_LOGIN_005_invalidTypes_shouldReturn400() {
    AuthLoginRequest body = new AuthLoginRequest(12345, true);

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .post("/auth/login")
      .then()
        .statusCode(400)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "AUTH_LOGIN_006 - Login fails when body is empty JSON object.")
  public void AUTH_LOGIN_006_emptyBody_shouldReturn400() {
    AuthLoginRequest body = new AuthLoginRequest();

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .post("/auth/login")
      .then()
        .statusCode(400)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }
}
