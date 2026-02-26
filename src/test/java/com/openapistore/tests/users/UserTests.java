package com.openapistore.tests.users;

import com.openapistore.models.user.*;
import com.openapistore.tests.BaseApiTest;
import com.openapistore.utils.RandomDataUtil;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class UserTests extends BaseApiTest {

  @Test(description = "USR_GETALL_001 - Retrieve all users successfully.")
  public void USR_GETALL_001_getAllUsers_shouldReturnArray() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .get("/users")
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.List.class))
        .body("size()", greaterThanOrEqualTo(1))
        .body("[0].id", instanceOf(Number.class))
        .body("[0].email", instanceOf(String.class))
        .body("[0].username", instanceOf(String.class))
        .body(matchesJsonSchemaInClasspath("schemas/users_schema.json"));
  }

  @Test(description = "USR_GETALL_002 - Verify user includes nested name and address objects.")
  public void USR_GETALL_002_usersShouldContainNestedNameAndAddress() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .get("/users")
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("[0].name", instanceOf(java.util.Map.class))
        .body("[0].name.firstname", instanceOf(String.class))
        .body("[0].address", instanceOf(java.util.Map.class))
        .body("[0].address.geolocation", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/users_schema.json"));
  }

  @Test(description = "USR_POST_001 - Create a user with valid payload.")
  public void USR_POST_001_createUser_validPayload_shouldSucceed() {
    UserCreateRequest body = new UserCreateRequest(
        "new.user@example.com",
        "new_user_001",
        "Passw0rd!",
        new Name("New", "User"),
        new Address("TestCity", "Test Street", 10, "12345", new GeoLocation("10.0000", "20.0000")),
        "1-111-111-1111"
    );

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .post("/users")
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("id", instanceOf(Number.class))
        .body("username", equalTo("new_user_001"))
        .body("email", equalTo("new.user@example.com"))
        .body(matchesJsonSchemaInClasspath("schemas/user_schema.json"));
  }

  @Test(description = "USR_POST_002 - Create user fails when email is missing.")
  public void USR_POST_002_createUser_missingEmail_shouldReturn400() {
    UserCreateRequest body = new UserCreateRequest(null, "new_user_002", "Passw0rd!", null, null, null);

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .post("/users")
      .then()
        .statusCode(400)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "USR_POST_003 - Create user fails when email format is invalid.")
  public void USR_POST_003_createUser_invalidEmail_shouldReturn400() {
    UserCreateRequest body = new UserCreateRequest("not-an-email", "new_user_003", "Passw0rd!", null, null, null);

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .post("/users")
      .then()
        .statusCode(400)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "USR_POST_004 - Edge: Create user with very long username.")
  public void USR_POST_004_createUser_longUsername_shouldReturn400() {
    String longUsername = RandomDataUtil.repeat("u", 256);
    UserCreateRequest body = new UserCreateRequest("long.username@example.com", longUsername, "Passw0rd!", null, null, null);

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .post("/users")
      .then()
        .statusCode(400)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "USR_GETBYID_001 - Retrieve a user by valid existing ID.")
  public void USR_GETBYID_001_getUserById_shouldReturnUser() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .get("/users/{id}", 1)
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("id", equalTo(1))
        .body("email", instanceOf(String.class))
        .body("username", instanceOf(String.class))
        .body(matchesJsonSchemaInClasspath("schemas/user_schema.json"));
  }

  @Test(description = "USR_GETBYID_002 - Get user returns 404 for non-existent ID.")
  public void USR_GETBYID_002_getUserByNonExistentId_shouldReturn404() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .get("/users/{id}", 999999)
      .then()
        .statusCode(404)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "USR_PUT_001 - Update an existing user with valid payload.")
  public void USR_PUT_001_updateUser_valid_shouldSucceed() {
    UserUpdateRequest body = new UserUpdateRequest("john.updated@example.com", "johnd_updated");

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .put("/users/{id}", 1)
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("id", instanceOf(Number.class))
        .body("email", equalTo("john.updated@example.com"))
        .body("username", equalTo("johnd_updated"))
        .body(matchesJsonSchemaInClasspath("schemas/user_schema.json"));
  }

  @Test(description = "USR_PUT_002 - Update fails when user id does not exist.")
  public void USR_PUT_002_updateUser_nonExistentId_shouldReturn404() {
    UserUpdateRequest body = new UserUpdateRequest("nobody@example.com", null);

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .put("/users/{id}", 999999)
      .then()
        .statusCode(404)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "USR_PUT_003 - Update fails when email format is invalid.")
  public void USR_PUT_003_updateUser_invalidEmail_shouldReturn400() {
    UserUpdateRequest body = new UserUpdateRequest("bad_email_format", null);

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .put("/users/{id}", 1)
      .then()
        .statusCode(400)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "USR_DEL_001 - Delete existing user by ID.")
  public void USR_DEL_001_deleteUser_existingId_shouldSucceed() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .delete("/users/{id}", 1)
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("id", instanceOf(Number.class))
        .body(matchesJsonSchemaInClasspath("schemas/user_schema.json"));
  }

  @Test(description = "USR_DEL_002 - Delete returns 404 for non-existent user ID.")
  public void USR_DEL_002_deleteUser_nonExistentId_shouldReturn404() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .delete("/users/{id}", 999999)
      .then()
        .statusCode(404)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }
}
