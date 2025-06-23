package com.broker.price.opinion.dto.DTAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParsedStreetAddress {

    @JsonProperty("DirectionPrefix")
    public String DirectionPrefix;

    @JsonProperty("StandardizedHouseNumber")
    public Integer StandardizedHouseNumber;

    @JsonProperty("StandardizedHouseNumberString")
    public String StandardizedHouseNumberString;

    @JsonProperty("StreetName")
    public String StreetName;

    @JsonProperty("StreetSuffix")
    public String StreetSuffix;

    @JsonProperty("DirectionSuffix")
    public String DirectionSuffix;

    @JsonProperty("ApartmentOrUnit")
    public String ApartmentOrUnit;
}