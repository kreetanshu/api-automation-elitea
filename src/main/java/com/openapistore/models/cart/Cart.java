package com.openapistore.models.cart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Cart {
  private Integer id;
  private Integer userId;
  private String date;
  private List<CartProduct> products;

  public Cart() {}

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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

  public List<CartProduct> getProducts() {
    return products;
  }

  public void setProducts(List<CartProduct> products) {
    this.products = products;
  }
}
