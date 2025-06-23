package com.broker.price.opinion.dto.DTAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubjectProperty {

    @JsonProperty("PropertyId")
    public Long PropertyId;

    @JsonProperty("SitusAddress")
    public SitusAddress SitusAddress;

    @JsonProperty("ParsedStreetAddress")
    public ParsedStreetAddress ParsedStreetAddress;
}