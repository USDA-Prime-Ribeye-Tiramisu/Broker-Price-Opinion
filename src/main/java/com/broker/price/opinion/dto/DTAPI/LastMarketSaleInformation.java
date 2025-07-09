package com.broker.price.opinion.dto.DTAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LastMarketSaleInformation {

    @JsonProperty("DeedType")
    public String DeedType;

    @JsonProperty("SaleDate")
    public String SaleDate;

    @JsonProperty("RecordingDate")
    public String RecordingDate;

    @JsonProperty("SalePrice")
    public Double SalePrice;

    @JsonProperty("SaleType")
    public String SaleType;

    @JsonProperty("MultiOrSplitSaleIdentifier")
    public String MultiOrSplitSaleIdentifier;

    @JsonProperty("BuyerName")
    public String BuyerName;

    @JsonProperty("SellerName")
    public String SellerName;

    @JsonProperty("PricePerSquareFoot")
    public Double PricePerSquareFoot;

    @JsonProperty("TransferDocumentNumber")
    public String TransferDocumentNumber;

    @JsonProperty("TransferDocumentCmt")
    public Integer TransferDocumentCmt;

    @JsonProperty("TransferDocumentNumberCmtId")
    public String TransferDocumentNumberCmtId;

    @JsonProperty("BookPage")
    public String BookPage;

    @JsonProperty("FirstMortgageAmount")
    public Double FirstMortgageAmount;

    @JsonProperty("FirstMortgageType")
    public String FirstMortgageType;

    @JsonProperty("FirstMortgageTypeDescription")
    public String FirstMortgageTypeDescription;

    @JsonProperty("FirstMortgageInterestRate")
    public Double FirstMortgageInterestRate;

    @JsonProperty("FirstMortgageInterestType")
    public String FirstMortgageInterestType;

    @JsonProperty("FirstMortgageDocumentNumber")
    public String FirstMortgageDocumentNumber;

    @JsonProperty("FirstMortgageDocumentCmt")
    public Integer FirstMortgageDocumentCmt;

    @JsonProperty("NewConstruction")
    public String NewConstruction;

    @JsonProperty("Lender")
    public String Lender;

    @JsonProperty("TitleCompany")
    public String TitleCompany;

    @JsonProperty("SecondMortgageAmount")
    public Double SecondMortgageAmount;

    @JsonProperty("SecondMortgageType")
    public String SecondMortgageType;

    @JsonProperty("SecondMortgageInterestRate")
    public Double SecondMortgageInterestRate;

    @JsonProperty("SecondMortgageInterestType")
    public String SecondMortgageInterestType;

    @JsonProperty("LastMarketSaleVerified")
    public Boolean LastMarketSaleVerified;

    @JsonProperty("CurrentThroughDate")
    public String CurrentThroughDate;
}