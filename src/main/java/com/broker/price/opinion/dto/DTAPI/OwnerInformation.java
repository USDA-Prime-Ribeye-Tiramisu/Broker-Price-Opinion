package com.broker.price.opinion.dto.DTAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OwnerInformation {

    @JsonProperty("OwnerNames")
    public String OwnerNames;

    @JsonProperty("Owner1FullName")
    public String Owner1FullName;

    @JsonProperty("Owner2FullName")
    public String Owner2FullName;

    @JsonProperty("Owner3FullName")
    public String Owner3FullName;

    @JsonProperty("Owner4FullName")
    public String Owner4FullName;

    @JsonProperty("OwnerVestingInfo")
    public OwnerVestingInfo OwnerVestingInfo;

    @JsonProperty("OwnerOccupiedIndicator")
    public String OwnerOccupiedIndicator;

    @JsonProperty("Occupancy")
    public String Occupancy;

    @JsonProperty("MailingAddress")
    public MailingAddress MailingAddress;
}