package com.broker.price.opinion.dto.DTAPI.response;

import com.broker.price.opinion.dto.DTAPI.Report;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyDetailReportResponse {

    @JsonProperty("StatusDescription")
    public String StatusDescription;

    @JsonProperty("MaxResultsCount")
    public Integer MaxResultsCount;

    @JsonProperty("LitePropertyList")
    public Object LitePropertyList;

    @JsonProperty("Reports")
    public List<Report> Reports;
}