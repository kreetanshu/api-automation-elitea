package com.openapistore.models.cart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartCreateRequest {
  private Integer userId;
  private String date;
  private Object products;

  public CartCreateRequest() {}

  public CartCreateRequest(Integer userId, String date, List<CartProduct> products) {
    this.userId = userId;
    this.date = date;
    this.products = products;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Object getProducts() {
    return products;
  }

  public void setProducts(Object products) {
    this.products = products;
  }
}
