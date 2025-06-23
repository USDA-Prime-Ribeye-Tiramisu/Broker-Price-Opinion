package com.broker.price.opinion.dto.DTAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyCharacteristics {

    @JsonProperty("GrossArea")
    public Integer GrossArea;

    @JsonProperty("LivingArea")
    public Integer LivingArea;

    @JsonProperty("TotalAdjustedArea")
    public String TotalAdjustedArea;

    @JsonProperty("AboveGrade")
    public String AboveGrade;

    @JsonProperty("TotalRooms")
    public Integer TotalRooms;

    @JsonProperty("Bedrooms")
    public Integer Bedrooms;

    @JsonProperty("Baths")
    public String Baths;

    @JsonProperty("FullBath")
    public Integer FullBath;

    @JsonProperty("HalfBath")
    public Integer HalfBath;

    @JsonProperty("YearBuilt")
    public Integer YearBuilt;

    @JsonProperty("EFFYear")
    public String EFFYear;

    @JsonProperty("FirePlaceCount")
    public Integer FirePlaceCount;

    @JsonProperty("FirePlaceIndicator")
    public String FirePlaceIndicator;

    @JsonProperty("NumberOfStories")
    public Double NumberOfStories;

    @JsonProperty("ParkingType")
    public String ParkingType;

    @JsonProperty("ParkingSpace")
    public String ParkingSpace;

    @JsonProperty("GarageArea")
    public Integer GarageArea;

    @JsonProperty("GarageCapacity")
    public Integer GarageCapacity;

    @JsonProperty("BasementArea")
    public Integer BasementArea;

    @JsonProperty("BasementType")
    public String BasementType;

    @JsonProperty("RoofType")
    public String RoofType;

    @JsonProperty("Foundation")
    public String Foundation;

    @JsonProperty("RoofMaterial")
    public String RoofMaterial;

    @JsonProperty("ConstructType")
    public String ConstructType;

    @JsonProperty("ExteriorWall")
    public String ExteriorWall;

    @JsonProperty("PorchType")
    public String PorchType;

    @JsonProperty("PatioType")
    public String PatioType;

    @JsonProperty("Pool")
    public String Pool;

    @JsonProperty("AirConditioning")
    public String AirConditioning;

    @JsonProperty("HeatType")
    public String HeatType;

    @JsonProperty("Style")
    public String Style;

    @JsonProperty("Quality")
    public String Quality;

    @JsonProperty("Condition")
    public String Condition;

    @JsonProperty("ElementarySchoolId")
    public String ElementarySchoolId;

    @JsonProperty("MiddleSchoolId")
    public String MiddleSchoolId;

    @JsonProperty("HighSchoolId")
    public String HighSchoolId;
}