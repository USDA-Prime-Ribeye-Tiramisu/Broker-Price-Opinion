package com.broker.price.opinion.dto.DTAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriorSaleInformation {

    @JsonProperty("PriorDeedType")
    public String PriorDeedType;

    @JsonProperty("PriorSaleDate")
    public String PriorSaleDate;

    @JsonProperty("PriorRecordingDate")
    public String PriorRecordingDate;

    @JsonProperty("PriorSalePrice")
    public Double PriorSalePrice;

    @JsonProperty("PriorSaleTypeDescription")
    public String PriorSaleTypeDescription;

    @JsonProperty("PriorBuyerName")
    public String PriorBuyerName;

    @JsonProperty("PriorSellerName")
    public String PriorSellerName;

    @JsonProperty("PriorFirstMortgageAmount")
    public Double PriorFirstMortgageAmount;

    @JsonProperty("PriorFirstMortgageType")
    public String PriorFirstMortgageType;

    @JsonProperty("PriorFirstMortgageInterestRate")
    public Double PriorFirstMortgageInterestRate;

    @JsonProperty("PriorFirstMortgageInterestType")
    public String PriorFirstMortgageInterestType;

    @JsonProperty("PriorLender")
    public String PriorLender;

    @JsonProperty("PriorDocNumber")
    public String PriorDocNumber;

    @JsonProperty("PriorDocCmt")
    public Integer PriorDocCmt;

    @JsonProperty("PriorSaleInfoVerified")
    public Boolean PriorSaleInfoVerified;

    @JsonProperty("CurrentThroughDate")
    public String CurrentThroughDate;
}