package com.openapistore.models.cart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartProduct {
  private Integer productId;
  private Object quantity;

  public CartProduct() {}

  public CartProduct(Integer productId, Object quantity) {
    this.productId = productId;
    this.quantity = quantity;
  }

  public Integer getProductId() {
    return productId;
  }

  public void setProductId(Integer productId) {
    this.productId = productId;
  }

  public Object getQuantity() {
    return quantity;
  }

  public void setQuantity(Object quantity) {
    this.quantity = quantity;
  }
}
