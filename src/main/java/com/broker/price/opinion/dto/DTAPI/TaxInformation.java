package com.broker.price.opinion.dto.DTAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaxInformation {

    @JsonProperty("TaxYear")
    public Integer TaxYear;

    @JsonProperty("TotalTaxableValue")
    public Double TotalTaxableValue;

    @JsonProperty("PropertyTax")
    public Double PropertyTax;

    @JsonProperty("TaxArea")
    public String TaxArea;

    @JsonProperty("TaxExemption")
    public String TaxExemption;

    @JsonProperty("DelinquentYear")
    public Integer DelinquentYear;

    @JsonProperty("AssessedYear")
    public Integer AssessedYear;

    @JsonProperty("AssessedValue")
    public Double AssessedValue;

    @JsonProperty("LandValue")
    public Double LandValue;

    @JsonProperty("ImprovementValue")
    public Double ImprovementValue;

    @JsonProperty("ImprovedPercent")
    public Double ImprovedPercent;

    @JsonProperty("MarketValue")
    public Double MarketValue;

    @JsonProperty("MarketLandValue")
    public Double MarketLandValue;

    @JsonProperty("MarketImprovValue")
    public Double MarketImprovValue;

    @JsonProperty("MarketImprovValuePercent")
    public String MarketImprovValuePercent;
}