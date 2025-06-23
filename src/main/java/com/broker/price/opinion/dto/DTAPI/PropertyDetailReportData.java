package com.broker.price.opinion.dto.DTAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyDetailReportData {

    @JsonProperty("SubjectProperty")
    public SubjectProperty SubjectProperty;

    @JsonProperty("OwnerInformation")
    public OwnerInformation OwnerInformation;

    @JsonProperty("LocationInformation")
    public LocationInformation LocationInformation;

    @JsonProperty("SiteInformation")
    public SiteInformation SiteInformation;

    @JsonProperty("PropertyCharacteristics")
    public PropertyCharacteristics PropertyCharacteristics;

    @JsonProperty("TaxInformation")
    public TaxInformation TaxInformation;

    @JsonProperty("CountyRecordingHistory")
    public CountyRecordingHistory CountyRecordingHistory;

    @JsonProperty("OwnerTransferInformation")
    public OwnerTransferInformation OwnerTransferInformation;

    @JsonProperty("LastMarketSaleInformation")
    public LastMarketSaleInformation LastMarketSaleInformation;

    @JsonProperty("PriorSaleInformation")
    public PriorSaleInformation PriorSaleInformation;

    @JsonProperty("FileLink")
    public String FileLink;
}