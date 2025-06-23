package com.broker.price.opinion.dto.DTAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteInformation {

    @JsonProperty("Zoning")
    public String Zoning;

    @JsonProperty("LandUse")
    public String LandUse;

    @JsonProperty("CountyUse")
    public String CountyUse;

    @JsonProperty("CountyUseCode")
    public String CountyUseCode;

    @JsonProperty("StateUse")
    public String StateUse;

    @JsonProperty("StateUseCode")
    public String StateUseCode;

    @JsonProperty("SiteInfluence")
    public String SiteInfluence;

    @JsonProperty("NumberOfBuildings")
    public Integer NumberOfBuildings;

    @JsonProperty("UnitsResidential")
    public Integer UnitsResidential;

    @JsonProperty("UnitsCommercial")
    public String UnitsCommercial;

    @JsonProperty("WaterType")
    public String WaterType;

    @JsonProperty("SewerType")
    public String SewerType;

    @JsonProperty("Acres")
    public Double Acres;

    @JsonProperty("LotArea")
    public Double LotArea;

    @JsonProperty("LotWidth")
    public String LotWidth;

    @JsonProperty("LotDepth")
    public String LotDepth;

    @JsonProperty("UsableLot")
    public String UsableLot;

    @JsonProperty("FloodZoneCode")
    public String FloodZoneCode;

    @JsonProperty("FloodMap")
    public String FloodMap;

    @JsonProperty("FloodMapDate")
    public String FloodMapDate;

    @JsonProperty("FloodPanel")
    public String FloodPanel;

    @JsonProperty("CommunityName")
    public String CommunityName;

    @JsonProperty("CommunityID")
    public String CommunityID;

    @JsonProperty("InsideSFHA")
    public String InsideSFHA;
}