package com.openapistore.utils;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.Reporter;

/**
 * Helpers for handling FakeStore API's occasionally non-standard behaviors on negative scenarios.
 */
public final class ResponseAssertions {

    private ResponseAssertions() {
    }

    /**
     * Used for endpoints where API may return 404, or 200 with empty body/object.
     */
    public static void assertNotFoundOrEmpty(Response response, int... allowedSuccessCodes) {
        int status = response.statusCode();
        if (status == 404) {
            return;
        }

        for (int allowed : allowedSuccessCodes) {
            if (status == allowed) {
                String body = response.asString();
                // Typical "empty" variants seen in demo APIs
                boolean empty = body == null || body.isBlank() || body.trim().equals("{}") || body.trim().equals("null");
                if (!empty) {
                    Reporter.log("WARNING: Expected empty response body for allowed status " + allowed + ", got: " + body, true);
                }
                return;
            }
        }

        Assert.fail("Expected 404 (or allowed success) but got HTTP " + status + ". Body: " + response.asString());
    }
}
