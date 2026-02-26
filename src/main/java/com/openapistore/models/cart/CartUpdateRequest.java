package com.openapistore.models.cart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartUpdateRequest {
  private Integer userId;
  private Object products;

  public CartUpdateRequest() {}

  public CartUpdateRequest(Integer userId, List<CartProduct> products) {
    this.userId = userId;
    this.products = products;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public Object getProducts() {
    return products;
  }

  public void setProducts(Object products) {
    this.products = products;
  }
}
