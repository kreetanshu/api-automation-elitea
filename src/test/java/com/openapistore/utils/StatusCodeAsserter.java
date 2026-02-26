package com.openapistore.utils;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.Reporter;

import java.util.Arrays;

public final class StatusCodeAsserter {
    private StatusCodeAsserter() {
    }

    /**
     * Asserts that response status is the expected one. If the status equals one of the allowed deviations,
     * the test is not failed, but a warning is logged.
     */
    public static void assertStatus(Response response, int expected, int... allowedDeviations) {
        int actual = response.statusCode();
        if (actual == expected) {
            return;
        }

        for (int deviation : allowedDeviations) {
            if (actual == deviation) {
                Reporter.log("KNOWN DEVIATION: Expected HTTP " + expected + " but got HTTP " + actual
                        + " (allowed deviations: " + Arrays.toString(allowedDeviations) + ")", true);
                return;
            }
        }

        Assert.fail("Unexpected HTTP status. Expected " + expected + " (or allowed deviations "
                + Arrays.toString(allowedDeviations) + ") but got " + actual
                + ". Response body: " + response.asString());
    }
}
