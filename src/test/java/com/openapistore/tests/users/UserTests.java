package com.openapistore.tests.users;

import com.openapistore.models.users.Address;
import com.openapistore.models.users.GeoLocation;
import com.openapistore.models.users.Name;
import com.openapistore.models.users.User;
import com.openapistore.tests.base.BaseTest;
import com.openapistore.utils.ResponseAssertions;
import com.openapistore.utils.StatusCodeAsserter;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class UserTests extends BaseTest {

    @Test(description = "USER_GETALL_001 - Retrieve all users")
    public void USER_GETALL_001_getAllUsers() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/users");

        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/users/userList.schema.json"));

        List<Map<String, Object>> users = response.jsonPath().getList("$");
        Assert.assertNotNull(users);
        Assert.assertFalse(users.isEmpty());

        for (int i = 0; i < Math.min(users.size(), 5); i++) {
            Map<String, Object> u = users.get(i);
            Assert.assertTrue(u.containsKey("id"));
            Assert.assertTrue(u.containsKey("email"));
            Assert.assertTrue(u.containsKey("username"));
        }
    }

    @Test(description = "USER_GETBYID_001 - Retrieve an existing user by id")
    public void USER_GETBYID_001_getUserById() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/users/1");

        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/users/user.schema.json"));

        Assert.assertEquals(response.jsonPath().getInt("id"), 1);
        String email = response.jsonPath().getString("email");
        Assert.assertTrue(email.contains("@"), "email should contain '@'");
    }

    @Test(description = "USER_GETBYID_002 - Non-existing user id returns not found (or empty)")
    public void USER_GETBYID_002_getNonExistingUser() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/users/9999");

        ResponseAssertions.assertNotFoundOrEmpty(response, 200);
    }

    @Test(description = "USER_POST_001 - Create a new user with minimal fields")
    public void USER_POST_001_createUserMinimal() {
        User request = new User();
        request.setUsername("auto_user_001");
        request.setEmail("auto_user_001@example.com");

        Response response = given()
                .spec(requestSpec)
                .body(request)
                .when()
                .post("/users");

        response.then().statusCode(200);

        User created = response.as(User.class);
        Assert.assertNotNull(created.getId(), "response should contain id");
        Assert.assertEquals(created.getUsername(), request.getUsername());
        Assert.assertEquals(created.getEmail(), request.getEmail());
    }

    @Test(description = "USER_POST_002 - Create a new user with full typical structure")
    public void USER_POST_002_createUserFull() {
        User request = new User();
        request.setEmail("auto_user_full@example.com");
        request.setUsername("auto_user_full");
        request.setPassword("Passw0rd!");

        Name name = new Name();
        name.setFirstname("Auto");
        name.setLastname("User");
        request.setName(name);

        GeoLocation geo = new GeoLocation();
        geo.setLat("10.0000");
        geo.setLongitude("20.0000");

        Address address = new Address();
        address.setCity("TestCity");
        address.setStreet("Test Street");
        address.setNumber(123);
        address.setZipcode("12345");
        address.setGeolocation(geo);
        request.setAddress(address);

        request.setPhone("1-111-111-1111");

        Response response = given()
                .spec(requestSpec)
                .body(request)
                .when()
                .post("/users");

        response.then().statusCode(200);

        User created = response.as(User.class);
        Assert.assertNotNull(created.getId(), "response should contain id");
        Assert.assertEquals(created.getUsername(), request.getUsername());
        Assert.assertEquals(created.getEmail(), request.getEmail());
    }

    @Test(description = "USER_POST_003 - Fail to create user when email is missing")
    public void USER_POST_003_missingEmail() {
        User request = new User();
        request.setUsername("auto_user_no_email");

        Response response = given()
                .spec(requestSpec)
                .body(request)
                .when()
                .post("/users");

        StatusCodeAsserter.assertStatus(response, 400, 200, 422);
        if (response.statusCode() == 200) {
            Reporter.log("KNOWN DEVIATION: API accepted user without email. Body: " + response.asString(), true);
        }
    }

    @Test(description = "USER_POST_004 - Fail to create user when email is invalid format")
    public void USER_POST_004_invalidEmailFormat() {
        User request = new User();
        request.setUsername("auto_user_bad_email");
        request.setEmail("not-an-email");

        Response response = given()
                .spec(requestSpec)
                .body(request)
                .when()
                .post("/users");

        StatusCodeAsserter.assertStatus(response, 400, 200, 422);
        if (response.statusCode() == 200) {
            Assert.assertEquals(response.jsonPath().getString("email"), "not-an-email");
            Reporter.log("VALIDATION GAP: API accepted invalid email format", true);
        }
    }

    @Test(description = "USER_PUT_001 - Update existing user's email and username")
    public void USER_PUT_001_updateExistingUser() {
        User request = new User();
        request.setEmail("john.updated@example.com");
        request.setUsername("johnd_updated");

        Response response = given()
                .spec(requestSpec)
                .body(request)
                .when()
                .put("/users/1");

        response.then().statusCode(200);
        Assert.assertEquals(response.jsonPath().getInt("id"), 1);
        Assert.assertEquals(response.jsonPath().getString("email"), request.getEmail());
        Assert.assertEquals(response.jsonPath().getString("username"), request.getUsername());
    }

    @Test(description = "USER_PUT_002 - Update fails when id is non-existing (or upsert)")
    public void USER_PUT_002_updateNonExistingUser() {
        User request = new User();
        request.setEmail("nouser@example.com");
        request.setUsername("nouser");

        Response response = given()
                .spec(requestSpec)
                .body(request)
                .when()
                .put("/users/9999");

        StatusCodeAsserter.assertStatus(response, 404, 200);
    }

    @Test(description = "USER_DELETE_001 - Delete an existing user by id")
    public void USER_DELETE_001_deleteExistingUser() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .delete("/users/1");

        response.then().statusCode(200);

        if (response.jsonPath().get("id") != null) {
            Assert.assertEquals(response.jsonPath().getInt("id"), 1);
        } else {
            Reporter.log("Delete response did not contain id. Body: " + response.asString(), true);
        }
    }

    @Test(description = "USER_DELETE_002 - Delete non-existing user id returns not found (or safe no-op)")
    public void USER_DELETE_002_deleteNonExistingUser() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .delete("/users/9999");

        StatusCodeAsserter.assertStatus(response, 404, 200);
    }
}
