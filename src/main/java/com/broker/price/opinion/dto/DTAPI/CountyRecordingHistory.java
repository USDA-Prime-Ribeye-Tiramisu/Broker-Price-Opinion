package com.broker.price.opinion.dto.DTAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountyRecordingHistory {

    @JsonProperty("SalesHistoryStartDate")
    public String SalesHistoryStartDate;

    @JsonProperty("SalesHistoryThroughDate")
    public String SalesHistoryThroughDate;

    @JsonProperty("MortgageHistoryStartDate")
    public String MortgageHistoryStartDate;

    @JsonProperty("MortgageHistoryThroughDate")
    public String MortgageHistoryThroughDate;

    @JsonProperty("AssignmentHistoryStartDate")
    public String AssignmentHistoryStartDate;

    @JsonProperty("AssignmentHistoryThroughDate")
    public String AssignmentHistoryThroughDate;

    @JsonProperty("ReleaseHistoryStartDate")
    public String ReleaseHistoryStartDate;

    @JsonProperty("ReleaseHistoryThroughDate")
    public String ReleaseHistoryThroughDate;

    @JsonProperty("ForeclosureHistoryStartDate")
    public String ForeclosureHistoryStartDate;

    @JsonProperty("ForeclosureHistoryThroughDate")
    public String ForeclosureHistoryThroughDate;

    @JsonProperty("StandAloneMortgageStartDate")
    public String StandAloneMortgageStartDate;

    @JsonProperty("StandAloneMortgageThroughDate")
    public String StandAloneMortgageThroughDate;
}