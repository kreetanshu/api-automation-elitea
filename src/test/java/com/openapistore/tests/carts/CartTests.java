package com.openapistore.tests.carts;

import com.openapistore.models.cart.CartCreateRequest;
import com.openapistore.models.cart.CartProduct;
import com.openapistore.models.cart.CartUpdateRequest;
import com.openapistore.tests.BaseApiTest;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class CartTests extends BaseApiTest {

  @Test(description = "CART_GETALL_001 - Retrieve all carts successfully.")
  public void CART_GETALL_001_getAllCarts_shouldReturnArray() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .get("/carts")
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.List.class))
        .body("size()", greaterThanOrEqualTo(1))
        .body("[0].id", instanceOf(Number.class))
        .body("[0].userId", instanceOf(Number.class))
        .body("[0].products", instanceOf(java.util.List.class))
        .body("[0].products.size()", greaterThanOrEqualTo(1))
        .body("[0].products[0].productId", instanceOf(Number.class))
        .body("[0].products[0].quantity", instanceOf(Number.class))
        .body(matchesJsonSchemaInClasspath("schemas/carts_schema.json"));
  }

  @Test(description = "CART_GETBYID_001 - Retrieve a cart by valid existing ID.")
  public void CART_GETBYID_001_getCartById_shouldReturnCart() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .get("/carts/{id}", 1)
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("id", equalTo(1))
        .body("userId", instanceOf(Number.class))
        .body("products", instanceOf(java.util.List.class))
        .body("products.size()", greaterThanOrEqualTo(1))
        .body(matchesJsonSchemaInClasspath("schemas/cart_schema.json"));
  }

  @Test(description = "CART_GETBYID_002 - Get cart returns 404 for non-existent ID.")
  public void CART_GETBYID_002_getCartByNonExistentId_shouldReturn404() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .get("/carts/{id}", 999999)
      .then()
        .statusCode(404)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "CART_POST_001 - Create a cart with valid payload (userId, date, products).")
  public void CART_POST_001_createCart_validPayload_shouldSucceed() {
    CartCreateRequest body = new CartCreateRequest(
        1,
        "2020-03-02T00:00:00.000Z",
        List.of(new CartProduct(1, 1), new CartProduct(2, 3))
    );

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .post("/carts")
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("id", instanceOf(Number.class))
        .body("userId", equalTo(1))
        .body("products", instanceOf(java.util.List.class))
        .body("products.size()", greaterThanOrEqualTo(1))
        .body(matchesJsonSchemaInClasspath("schemas/cart_schema.json"));
  }

  @Test(description = "CART_POST_002 - Create cart fails when products array is missing.")
  public void CART_POST_002_createCart_missingProducts_shouldReturn400() {
    CartCreateRequest body = new CartCreateRequest();
    body.setUserId(1);
    body.setDate("2020-03-02T00:00:00.000Z");
    body.setProducts(null);

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .post("/carts")
      .then()
        .statusCode(400)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "CART_POST_003 - Create cart fails when quantity is 0 (boundary invalid).")
  public void CART_POST_003_createCart_zeroQuantity_shouldReturn400() {
    CartCreateRequest body = new CartCreateRequest(
        1,
        null,
        List.of(new CartProduct(1, 0))
    );

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .post("/carts")
      .then()
        .statusCode(400)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "CART_PUT_001 - Update an existing cart with valid payload.")
  public void CART_PUT_001_updateCart_validPayload_shouldSucceed() {
    CartUpdateRequest body = new CartUpdateRequest(
        1,
        List.of(new CartProduct(3, 5))
    );

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .put("/carts/{id}", 1)
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("id", instanceOf(Number.class))
        .body("products[0].productId", equalTo(3))
        .body("products[0].quantity", equalTo(5))
        .body(matchesJsonSchemaInClasspath("schemas/cart_schema.json"));
  }

  @Test(description = "CART_PUT_002 - Update cart fails when id does not exist.")
  public void CART_PUT_002_updateCart_nonExistentId_shouldReturn404() {
    CartUpdateRequest body = new CartUpdateRequest(1, List.of(new CartProduct(1, 1)));

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .put("/carts/{id}", 999999)
      .then()
        .statusCode(404)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "CART_PUT_003 - Update cart fails when products is not an array (type mismatch).")
  public void CART_PUT_003_updateCart_productsNotArray_shouldReturn400() {
    CartUpdateRequest body = new CartUpdateRequest();
    body.setUserId(1);
    // Intentionally set products as an object (type mismatch)
    body.setProducts(new CartProduct(1, 1));

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .put("/carts/{id}", 1)
      .then()
        .statusCode(400)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "CART_DEL_001 - Delete existing cart by ID.")
  public void CART_DEL_001_deleteCart_existingId_shouldSucceed() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .delete("/carts/{id}", 1)
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("id", instanceOf(Number.class))
        .body(matchesJsonSchemaInClasspath("schemas/cart_schema.json"));
  }

  @Test(description = "CART_DEL_002 - Delete returns 404 for non-existent cart ID.")
  public void CART_DEL_002_deleteCart_nonExistentId_shouldReturn404() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .delete("/carts/{id}", 999999)
      .then()
        .statusCode(404)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }
}
