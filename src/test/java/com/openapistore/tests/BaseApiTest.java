package com.openapistore.tests;

import com.openapistore.client.RequestSpecFactory;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeSuite;

public abstract class BaseApiTest {

  @BeforeSuite(alwaysRun = true)
  public void beforeSuite() {
    // Ensures RestAssured.baseURI is configured and logging filters are enabled.
    RequestSpecFactory.get();
  }

  protected RequestSpecification spec() {
    return RequestSpecFactory.get();
  }
}
