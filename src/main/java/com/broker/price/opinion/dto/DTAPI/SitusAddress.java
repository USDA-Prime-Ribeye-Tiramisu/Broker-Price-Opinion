package com.broker.price.opinion.dto.DTAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SitusAddress {

    @JsonProperty("StreetAddress")
    public String StreetAddress;

    @JsonProperty("PreDirection")
    public String PreDirection;

    @JsonProperty("StreetType")
    public String StreetType;

    @JsonProperty("PostDirection")
    public String PostDirection;

    @JsonProperty("UnitNumber")
    public String UnitNumber;

    @JsonProperty("City")
    public String City;

    @JsonProperty("State")
    public String State;

    @JsonProperty("Zip9")
    public String Zip9;

    @JsonProperty("County")
    public String County;

    @JsonProperty("SitusCarrierRoute")
    public String SitusCarrierRoute;

    @JsonProperty("APN")
    public String APN;
}