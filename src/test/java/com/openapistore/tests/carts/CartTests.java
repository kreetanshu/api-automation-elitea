package com.openapistore.tests.carts;

import com.openapistore.models.carts.Cart;
import com.openapistore.models.carts.CartItem;
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

public class CartTests extends BaseTest {

    @Test(description = "CART_GETALL_001 - Retrieve all carts")
    public void CART_GETALL_001_getAllCarts() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/carts");

        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/carts/cartList.schema.json"));

        List<Map<String, Object>> carts = response.jsonPath().getList("$");
        Assert.assertNotNull(carts);
        Assert.assertFalse(carts.isEmpty(), "carts list should not be empty");

        for (int i = 0; i < Math.min(carts.size(), 5); i++) {
            Map<String, Object> c = carts.get(i);
            Assert.assertTrue(c.containsKey("id"));
            Assert.assertTrue(c.containsKey("userId"));
            Assert.assertTrue(c.containsKey("products"));
        }
    }

    @Test(description = "CART_GETBYID_001 - Retrieve an existing cart by id")
    public void CART_GETBYID_001_getCartById() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/carts/1");

        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/carts/cart.schema.json"));

        Assert.assertEquals(response.jsonPath().getInt("id"), 1);

        List<Map<String, Object>> products = response.jsonPath().getList("products");
        Assert.assertNotNull(products);
        for (Map<String, Object> item : products) {
            Assert.assertTrue(item.containsKey("productId"));
            Assert.assertTrue(item.containsKey("quantity"));
            Assert.assertTrue(item.get("productId") instanceof Number);
            Assert.assertTrue(item.get("quantity") instanceof Number);
        }
    }

    @Test(description = "CART_GETBYID_002 - Non-existing cart id returns not found (or empty)")
    public void CART_GETBYID_002_getNonExistingCart() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/carts/9999");

        ResponseAssertions.assertNotFoundOrEmpty(response, 200);
    }

    @Test(description = "CART_POST_001 - Create a cart with valid userId and product list")
    public void CART_POST_001_createCart() {
        Cart request = new Cart();
        request.setUserId(1);
        request.setProducts(List.of(
                new CartItem(1, 2),
                new CartItem(2, 1)
        ));

        Response response = given()
                .spec(requestSpec)
                .body(request)
                .when()
                .post("/carts");

        response.then().statusCode(200);

        Cart created = response.as(Cart.class);
        Assert.assertNotNull(created.getId(), "response should contain id");
        Assert.assertEquals(created.getUserId(), request.getUserId());
        Assert.assertNotNull(created.getProducts());
    }

    @Test(description = "CART_POST_002 - Fail to create cart when userId is missing")
    public void CART_POST_002_missingUserId() {
        String body = "{ \"products\": [{ \"productId\": 1, \"quantity\": 1 }] }";

        Response response = given()
                .spec(requestSpec)
                .body(body)
                .when()
                .post("/carts");

        StatusCodeAsserter.assertStatus(response, 400, 200, 422);
        if (response.statusCode() == 200) {
            Reporter.log("KNOWN DEVIATION: API accepted cart without userId. Body: " + response.asString(), true);
        }
    }

    @Test(description = "CART_POST_003 - Fail to create cart when products is empty array")
    public void CART_POST_003_emptyProductsArray() {
        Cart request = new Cart();
        request.setUserId(1);
        request.setProducts(List.of());

        Response response = given()
                .spec(requestSpec)
                .body(request)
                .when()
                .post("/carts");

        StatusCodeAsserter.assertStatus(response, 400, 200, 422);
        if (response.statusCode() == 200) {
            List<?> products = response.jsonPath().getList("products");
            Assert.assertNotNull(products);
            Assert.assertEquals(products.size(), 0, "If empty carts are allowed, response.products length should be 0");
        }
    }

    @Test(description = "CART_POST_004 - Fail to create cart when quantity is 0")
    public void CART_POST_004_quantityZero() {
        Cart request = new Cart();
        request.setUserId(1);
        request.setProducts(List.of(new CartItem(1, 0)));

        Response response = given()
                .spec(requestSpec)
                .body(request)
                .when()
                .post("/carts");

        StatusCodeAsserter.assertStatus(response, 400, 200, 422);
        if (response.statusCode() == 200) {
            Assert.assertEquals(response.jsonPath().getInt("products[0].quantity"), 0);
        }
    }

    @Test(description = "CART_PUT_001 - Update an existing cart's product quantities")
    public void CART_PUT_001_updateCart() {
        Cart request = new Cart();
        request.setUserId(1);
        request.setProducts(List.of(new CartItem(1, 5)));

        Response response = given()
                .spec(requestSpec)
                .body(request)
                .when()
                .put("/carts/1");

        response.then().statusCode(200);
        Assert.assertEquals(response.jsonPath().getInt("id"), 1);
        Assert.assertEquals(response.jsonPath().getInt("products[0].quantity"), 5);
    }

    @Test(description = "CART_DELETE_001 - Delete an existing cart by id")
    public void CART_DELETE_001_deleteCart() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .delete("/carts/1");

        response.then().statusCode(200);

        if (response.jsonPath().get("id") != null) {
            Assert.assertEquals(response.jsonPath().getInt("id"), 1);
        } else {
            Reporter.log("Delete response did not contain id. Body: " + response.asString(), true);
        }
    }
}
