package com.openapistore.tests.products;

import com.openapistore.models.products.Product;
import com.openapistore.tests.base.BaseTest;
import com.openapistore.utils.LongString;
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

public class ProductTests extends BaseTest {

    @Test(description = "PROD_GETALL_001 - Retrieve all products successfully")
    public void PROD_GETALL_001_getAllProducts() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/products");

        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/products/productList.schema.json"));

        List<Map<String, Object>> products = response.jsonPath().getList("$");
        Assert.assertNotNull(products);
        Assert.assertTrue(products.size() >= 1, "array length should be >= 1");

        // Validate first few items for expected keys and types
        for (int i = 0; i < Math.min(products.size(), 5); i++) {
            Map<String, Object> p = products.get(i);
            Assert.assertTrue(p.containsKey("id"));
            Assert.assertTrue(p.containsKey("title"));
            Assert.assertTrue(p.containsKey("price"));
            Assert.assertTrue(p.get("id") instanceof Number);
            Assert.assertTrue(p.get("title") instanceof String);
            Assert.assertTrue(p.get("price") instanceof Number);
        }
    }

    @Test(description = "PROD_GETALL_002 - Validate extended product fields")
    public void PROD_GETALL_002_validateExtendedFields() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/products");

        response.then().statusCode(200);

        List<Map<String, Object>> products = response.jsonPath().getList("$");
        Assert.assertTrue(products != null && !products.isEmpty(), "products list should not be empty");

        boolean found = false;
        for (Map<String, Object> p : products) {
            if (p.containsKey("description") && p.containsKey("category") && p.containsKey("image") && p.containsKey("rating")) {
                Object ratingObj = p.get("rating");
                if (ratingObj instanceof Map<?, ?> rating) {
                    found = rating.containsKey("rate") && rating.containsKey("count");
                }
            }
            if (found) break;
        }
        Assert.assertTrue(found, "at least one item should have description/category/image/rating with rate/count");
    }

    @Test(description = "PROD_POST_001 - Create new product with minimal fields")
    public void PROD_POST_001_createMinimalProduct() {
        Product request = new Product();
        request.setTitle("Automation Product - Minimal");
        request.setPrice(19.99);

        Response response = given()
                .spec(requestSpec)
                .body(request)
                .when()
                .post("/products");

        response.then().statusCode(200);

        Product created = response.as(Product.class);
        Assert.assertNotNull(created.getId(), "response should contain id");
        Assert.assertEquals(created.getTitle(), request.getTitle());
        Assert.assertEquals(created.getPrice(), request.getPrice(), 0.0001);
    }

    @Test(description = "PROD_POST_002 - Create new product with full typical fields")
    public void PROD_POST_002_createFullProduct() {
        Product request = new Product();
        request.setTitle("Automation Product - Full");
        request.setPrice(109.95);
        request.setDescription("Test description");
        request.setImage("https://example.com/img.png");
        request.setCategory("men's clothing");

        Response response = given()
                .spec(requestSpec)
                .body(request)
                .when()
                .post("/products");

        response.then().statusCode(200);

        Product created = response.as(Product.class);
        Assert.assertNotNull(created.getId(), "response should contain id");
        Assert.assertEquals(created.getTitle(), request.getTitle());
        Assert.assertEquals(created.getPrice(), request.getPrice(), 0.0001);
    }

    @Test(description = "PROD_POST_003 - Fail to create product when title is missing (may be accepted by FakeStore)")
    public void PROD_POST_003_missingTitle() {
        // Intentionally missing title
        String body = "{ \"price\": 10.0 }";

        Response response = given()
                .spec(requestSpec)
                .body(body)
                .when()
                .post("/products");

        // Some implementations accept and return 200; treat it as a known deviation.
        StatusCodeAsserter.assertStatus(response, 400, 200, 422);
        if (response.statusCode() == 200) {
            Reporter.log("KNOWN DEVIATION: API accepted product without title. Body: " + response.asString(), true);
        }
    }

    @Test(description = "PROD_POST_004 - Fail to create product when price is a string")
    public void PROD_POST_004_invalidPriceType() {
        String body = "{ \"title\": \"Invalid Price Type\", \"price\": \"19.99\" }";

        Response response = given()
                .spec(requestSpec)
                .body(body)
                .when()
                .post("/products");

        // Some APIs coerce types and return 200.
        StatusCodeAsserter.assertStatus(response, 400, 200, 422);
        if (response.statusCode() == 200) {
            // If coerced, ensure server returns numeric price.
            Object price = response.jsonPath().get("price");
            Assert.assertTrue(price instanceof Number, "If accepted, stored price should be numeric");
        }
    }

    @Test(description = "PROD_POST_005 - Edge: Create product with price = 0")
    public void PROD_POST_005_priceZero() {
        Product request = new Product();
        request.setTitle("Free Product");
        request.setPrice(0.0);

        Response response = given()
                .spec(requestSpec)
                .body(request)
                .when()
                .post("/products");

        response.then().statusCode(200);
        Assert.assertEquals(response.jsonPath().getDouble("price"), 0.0, 0.0001);
    }

    @Test(description = "PROD_POST_006 - Edge: Create product with very long title (1024 chars)")
    public void PROD_POST_006_veryLongTitle() {
        Product request = new Product();
        request.setTitle(LongString.repeat('X', 1024));
        request.setPrice(12.34);

        Response response = given()
                .spec(requestSpec)
                .body(request)
                .when()
                .post("/products");

        // If API enforces max length, it may return 400/422.
        StatusCodeAsserter.assertStatus(response, 200, 400, 422);
        if (response.statusCode() == 200) {
            Assert.assertEquals(response.jsonPath().getString("title"), request.getTitle());
        }
    }

    @Test(description = "PROD_GETBYID_001 - Retrieve existing product by id")
    public void PROD_GETBYID_001_getProductById() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/products/1");

        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/products/product.schema.json"));

        Assert.assertEquals(response.jsonPath().getInt("id"), 1);
    }

    @Test(description = "PROD_GETBYID_002 - Non-existing product id returns not found (or empty)")
    public void PROD_GETBYID_002_getNonExistingProduct() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/products/9999");

        // Some implementations return 200 with empty object.
        ResponseAssertions.assertNotFoundOrEmpty(response, 200);
    }

    @Test(description = "PROD_GETBYID_003 - Invalid path id (0) should be rejected or not found")
    public void PROD_GETBYID_003_getProductIdZero() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/products/0");

        ResponseAssertions.assertNotFoundOrEmpty(response, 200);
    }

    @Test(description = "PROD_GETBYID_004 - Invalid path id (negative) should be rejected or not found")
    public void PROD_GETBYID_004_getProductNegativeId() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/products/-1");

        ResponseAssertions.assertNotFoundOrEmpty(response, 200);
    }

    @Test(description = "PROD_PUT_001 - Update existing product title and price")
    public void PROD_PUT_001_updateProduct() {
        Product request = new Product();
        request.setTitle("Updated Title - Automation");
        request.setPrice(123.45);

        Response response = given()
                .spec(requestSpec)
                .body(request)
                .when()
                .put("/products/1");

        response.then().statusCode(200);
        Assert.assertEquals(response.jsonPath().getInt("id"), 1);
        Assert.assertEquals(response.jsonPath().getString("title"), request.getTitle());
        Assert.assertEquals(response.jsonPath().getDouble("price"), request.getPrice(), 0.0001);
    }

    @Test(description = "PROD_PUT_002 - Update fails when price has invalid type")
    public void PROD_PUT_002_updateInvalidPriceType() {
        String body = "{ \"title\": \"Updated Title - Invalid Price Type\", \"price\": \"abc\" }";

        Response response = given()
                .spec(requestSpec)
                .body(body)
                .when()
                .put("/products/1");

        StatusCodeAsserter.assertStatus(response, 400, 200, 422);
        if (response.statusCode() == 200) {
            Object price = response.jsonPath().get("price");
            Assert.assertTrue(price instanceof Number, "If accepted, stored price should be numeric");
        }
    }

    @Test(description = "PROD_PUT_003 - Update non-existing product id should return not found or upsert")
    public void PROD_PUT_003_updateNonExistingProduct() {
        Product request = new Product();
        request.setTitle("Update Non-Existing");
        request.setPrice(10.0);

        Response response = given()
                .spec(requestSpec)
                .body(request)
                .when()
                .put("/products/9999");

        // Some APIs upsert and return 200.
        StatusCodeAsserter.assertStatus(response, 404, 200);
    }

    @Test(description = "PROD_DELETE_001 - Delete existing product by id")
    public void PROD_DELETE_001_deleteExistingProduct() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .delete("/products/1");

        response.then().statusCode(200);

        // API often returns deleted entity.
        if (response.jsonPath().get("id") != null) {
            Assert.assertEquals(response.jsonPath().getInt("id"), 1);
        } else {
            Reporter.log("Delete response did not contain id. Body: " + response.asString(), true);
        }
    }

    @Test(description = "PROD_DELETE_002 - Delete non-existing product id should return not found (or safe no-op)")
    public void PROD_DELETE_002_deleteNonExistingProduct() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .delete("/products/9999");

        StatusCodeAsserter.assertStatus(response, 404, 200);
    }
}
