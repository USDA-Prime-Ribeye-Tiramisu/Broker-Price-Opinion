package com.broker.price.opinion.dto.DTAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Report {

    @JsonProperty("PropertyId")
    public Long PropertyId;

    @JsonProperty("ReportName")
    public String ReportName;

    @JsonProperty("ReportStatus")
    public String ReportStatus;

    @JsonProperty("ReferenceId")
    public String ReferenceId;

    @JsonProperty("OrderItemId")
    public long OrderItemId;

    @JsonProperty("Data")
    public PropertyDetailReportData Data;

    @JsonProperty("Message")
    public String Message;

    @JsonProperty("AdditionalReference")
    public String AdditionalReference;

    @JsonProperty("PriorOrderItemId")
    public Long PriorOrderItemId;
}