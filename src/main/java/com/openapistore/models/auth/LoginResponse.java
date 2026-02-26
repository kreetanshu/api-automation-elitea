package com.openapistore.models.auth;

/**
 * POJO used for /auth/login response deserialization.
 */
public class LoginResponse {
    private String token;

    public LoginResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
