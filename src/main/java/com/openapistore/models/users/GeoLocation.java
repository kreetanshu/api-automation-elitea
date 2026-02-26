package com.openapistore.models.users;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeoLocation {
    private String lat;

    @JsonProperty("long")
    private String longitude;

    public GeoLocation() {
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
