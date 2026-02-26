package com.openapistore.models.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthLoginRequest {
  private Object username;
  private Object password;

  public AuthLoginRequest() {}

  public AuthLoginRequest(Object username, Object password) {
    this.username = username;
    this.password = password;
  }

  public Object getUsername() {
    return username;
  }

  public void setUsername(Object username) {
    this.username = username;
  }

  public Object getPassword() {
    return password;
  }

  public void setPassword(Object password) {
    this.password = password;
  }
}
