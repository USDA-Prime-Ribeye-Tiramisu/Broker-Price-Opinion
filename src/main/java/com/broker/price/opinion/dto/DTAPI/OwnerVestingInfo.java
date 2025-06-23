package com.broker.price.opinion.dto.DTAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OwnerVestingInfo {

    @JsonProperty("VestingOwner")
    public String VestingOwner;

    @JsonProperty("VestingOwnershipRight")
    public String VestingOwnershipRight;

    @JsonProperty("VestingEtal")
    public String VestingEtal;
}