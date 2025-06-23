package com.broker.price.opinion.dto.DTAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OwnerTransferInformation {

    @JsonProperty("DeedType")
    public String DeedType;

    @JsonProperty("SaleDate")
    public String SaleDate;

    @JsonProperty("RecordingSaleDate")
    public String RecordingSaleDate;

    @JsonProperty("DocDate")
    public String DocDate;

    @JsonProperty("SalePrice")
    public Double SalePrice;

    @JsonProperty("TransferDocumentNumber")
    public String TransferDocumentNumber;

    @JsonProperty("TransferDocumentCmt")
    public Integer TransferDocumentCmt;

    @JsonProperty("FormattedTransferDocumentNumber")
    public String FormattedTransferDocumentNumber;

    @JsonProperty("FirstMortgageDocumentNumber")
    public String FirstMortgageDocumentNumber;

    @JsonProperty("BuyerName")
    public String BuyerName;

    @JsonProperty("SellerName")
    public String SellerName;

    @JsonProperty("CurrentThroughDate")
    public String CurrentThroughDate;
}