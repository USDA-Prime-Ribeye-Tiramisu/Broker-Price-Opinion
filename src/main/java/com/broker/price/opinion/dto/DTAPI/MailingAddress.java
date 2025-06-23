package com.broker.price.opinion.dto.DTAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MailingAddress {

    @JsonProperty("StreetAddress")
    public String StreetAddress;

    @JsonProperty("City")
    public String City;

    @JsonProperty("State")
    public String State;

    @JsonProperty("Zip9")
    public String Zip9;

    @JsonProperty("MailCarrierRoute")
    public String MailCarrierRoute;
}