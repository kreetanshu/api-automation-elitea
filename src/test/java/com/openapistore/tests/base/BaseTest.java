package com.openapistore.tests.base;

import com.openapistore.utils.Config;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

/**
 * Base class for all TestNG API tests.
 */
public abstract class BaseTest {

    protected RequestSpecification requestSpec;

    @BeforeClass(alwaysRun = true)
    public void setUpRestAssured() {
        RestAssured.baseURI = Config.getBaseUrl();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        requestSpec = new RequestSpecBuilder()
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .build();
    }
}
