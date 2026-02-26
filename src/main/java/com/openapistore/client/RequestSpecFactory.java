package com.openapistore.client;

import com.openapistore.config.Config;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Builds a reusable RestAssured RequestSpecification.
 */
public final class RequestSpecFactory {

  private static RequestSpecification spec;

  private RequestSpecFactory() {}

  public static synchronized RequestSpecification get() {
    if (spec == null) {
      Config config = Config.getInstance();

      RestAssured.baseURI = config.getBaseUrl();

      spec = new RequestSpecBuilder()
          .setAccept(ContentType.JSON)
          .addFilter(new RequestLoggingFilter(LogDetail.ALL))
          .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
          .build();
    }
    return spec;
  }
}
