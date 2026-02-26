package com.openapistore.models.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoLocation {
  private String lat;
  private String aLong;

  public GeoLocation() {}

  public GeoLocation(String lat, String aLong) {
    this.lat = lat;
    this.aLong = aLong;
  }

  public String getLat() {
    return lat;
  }

  public void setLat(String lat) {
    this.lat = lat;
  }

  // "long" is reserved keyword; FakeStore uses "long" as field name.
  @com.fasterxml.jackson.annotation.JsonProperty("long")
  public String getLong() {
    return aLong;
  }

  @com.fasterxml.jackson.annotation.JsonProperty("long")
  public void setLong(String aLong) {
    this.aLong = aLong;
  }
}
