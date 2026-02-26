package com.openapistore.models.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductCreateRequest {
  private String title;
  private Object price;
  private String description;
  private String image;
  private String category;

  public ProductCreateRequest() {}

  public ProductCreateRequest(String title, Object price, String description, String image, String category) {
    this.title = title;
    this.price = price;
    this.description = description;
    this.image = image;
    this.category = category;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Object getPrice() {
    return price;
  }

  public void setPrice(Object price) {
    this.price = price;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }
}
