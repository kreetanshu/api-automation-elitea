package com.openapistore.models.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthLoginResponse {
  private String token;

  public AuthLoginResponse() {}

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
