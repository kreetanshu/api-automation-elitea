package com.openapistore.tests.products;

import com.openapistore.models.product.ProductCreateRequest;
import com.openapistore.models.product.ProductUpdateRequest;
import com.openapistore.tests.BaseApiTest;
import com.openapistore.utils.RandomDataUtil;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class ProductTests extends BaseApiTest {

  @Test(description = "PRD_GETALL_001 - Retrieve all products successfully.")
  public void PRD_GETALL_001_getAllProducts_shouldReturnArray() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .get("/products")
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.List.class))
        .body("size()", greaterThanOrEqualTo(1))
        .body("[0].id", instanceOf(Number.class))
        .body("[0].title", allOf(instanceOf(String.class), not(isEmptyOrNullString())))
        .body("[0].price", instanceOf(Number.class))
        .body(matchesJsonSchemaInClasspath("schemas/products_schema.json"));
  }

  @Test(description = "PRD_GETALL_002 - Verify each product item contains expected nested rating object (rate, count).")
  public void PRD_GETALL_002_productsShouldContainRatingObject() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .get("/products")
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("[0].rating", instanceOf(java.util.Map.class))
        .body("[0].rating.rate", instanceOf(Number.class))
        .body("[0].rating.count", instanceOf(Number.class))
        .body(matchesJsonSchemaInClasspath("schemas/products_schema.json"));
  }

  @Test(description = "PRD_GETALL_003 - Validate products response is JSON array (not object).")
  public void PRD_GETALL_003_productsResponseShouldBeArray() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .get("/products")
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.List.class))
        .body(matchesJsonSchemaInClasspath("schemas/products_schema.json"));
  }

  @Test(description = "PRD_POST_001 - Create a product with valid payload.")
  public void PRD_POST_001_createProduct_validPayload_shouldSucceed() {
    ProductCreateRequest body = new ProductCreateRequest(
        "Test Product - Basic",
        19.99,
        "Test description",
        "https://example.com/img.png",
        "electronics"
    );

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .post("/products")
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("id", instanceOf(Number.class))
        .body("title", equalTo("Test Product - Basic"))
        .body("price", closeTo(19.99, 0.0001))
        .body(matchesJsonSchemaInClasspath("schemas/product_schema.json"));
  }

  @Test(description = "PRD_POST_002 - Create product fails when required fields are missing (e.g., title).")
  public void PRD_POST_002_createProduct_missingTitle_shouldReturn400() {
    ProductCreateRequest body = new ProductCreateRequest(null, 19.99, "Test description", null, null);

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .post("/products")
      .then()
        .statusCode(400)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "PRD_POST_003 - Create product fails when price type is invalid (string instead of number).")
  public void PRD_POST_003_createProduct_badPriceType_shouldReturn400() {
    ProductCreateRequest body = new ProductCreateRequest("Bad Price", "19.99", null, null, null);

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .post("/products")
      .then()
        .statusCode(400)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "PRD_POST_004 - Edge: Create product with boundary price = 0 (free item).")
  public void PRD_POST_004_createProduct_priceZero_shouldSucceed() {
    ProductCreateRequest body = new ProductCreateRequest("Free Item", 0, "Free", null, null);

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .post("/products")
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("price", anyOf(equalTo(0), equalTo(0.0f), equalTo(0.0d)))
        .body(matchesJsonSchemaInClasspath("schemas/product_schema.json"));
  }

  @Test(description = "PRD_POST_005 - Edge: Create product with very long title (boundary string length).")
  public void PRD_POST_005_createProduct_longTitle_shouldReturn400() {
    String longTitle = RandomDataUtil.repeat("X", 1024);
    ProductCreateRequest body = new ProductCreateRequest(longTitle, 10.5, null, null, null);

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .post("/products")
      .then()
        .statusCode(400)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "PRD_GETBYID_001 - Retrieve a product by valid existing ID.")
  public void PRD_GETBYID_001_getProductById_shouldReturnProduct() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .get("/products/{id}", 1)
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("id", equalTo(1))
        .body("title", allOf(instanceOf(String.class), not(isEmptyOrNullString())))
        .body("price", instanceOf(Number.class))
        .body(matchesJsonSchemaInClasspath("schemas/product_schema.json"));
  }

  @Test(description = "PRD_GETBYID_002 - Get product returns 404 for non-existent ID.")
  public void PRD_GETBYID_002_getProductByNonExistentId_shouldReturn404() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .get("/products/{id}", 999999)
      .then()
        .statusCode(404)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "PRD_GETBYID_003 - Get product returns 404 when id path param is not an integer.")
  public void PRD_GETBYID_003_getProductByNonIntegerId_shouldReturn404() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .get("/products/{id}", "abc")
      .then()
        .statusCode(404)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "PRD_PUT_001 - Update an existing product with valid payload.")
  public void PRD_PUT_001_updateProduct_valid_shouldSucceed() {
    ProductUpdateRequest body = new ProductUpdateRequest("Updated Title", 111.11);

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .put("/products/{id}", 1)
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("id", instanceOf(Number.class))
        .body("title", equalTo("Updated Title"))
        .body("price", closeTo(111.11, 0.0001))
        .body(matchesJsonSchemaInClasspath("schemas/product_schema.json"));
  }

  @Test(description = "PRD_PUT_002 - Update product fails when id does not exist.")
  public void PRD_PUT_002_updateProduct_nonExistentId_shouldReturn404() {
    ProductUpdateRequest body = new ProductUpdateRequest("Does Not Matter", 10);

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .put("/products/{id}", 999999)
      .then()
        .statusCode(404)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "PRD_PUT_003 - Update product fails when price is negative (boundary validation).")
  public void PRD_PUT_003_updateProduct_negativePrice_shouldReturn400() {
    ProductUpdateRequest body = new ProductUpdateRequest(null, -1);

    given()
        .spec(spec())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(body)
      .when()
        .put("/products/{id}", 1)
      .then()
        .statusCode(400)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }

  @Test(description = "PRD_DEL_001 - Delete existing product by ID.")
  public void PRD_DEL_001_deleteProduct_existingId_shouldSucceed() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .delete("/products/{id}", 1)
      .then()
        .statusCode(200)
        .header("Content-Type", containsString("application/json"))
        .body("id", instanceOf(Number.class))
        .body(matchesJsonSchemaInClasspath("schemas/product_schema.json"));
  }

  @Test(description = "PRD_DEL_002 - Delete returns 404 for non-existent product ID.")
  public void PRD_DEL_002_deleteProduct_nonExistentId_shouldReturn404() {
    given()
        .spec(spec())
        .accept(ContentType.JSON)
      .when()
        .delete("/products/{id}", 999999)
      .then()
        .statusCode(404)
        .header("Content-Type", containsString("application/json"))
        .body("", instanceOf(java.util.Map.class))
        .body(matchesJsonSchemaInClasspath("schemas/error_schema.json"));
  }
}
