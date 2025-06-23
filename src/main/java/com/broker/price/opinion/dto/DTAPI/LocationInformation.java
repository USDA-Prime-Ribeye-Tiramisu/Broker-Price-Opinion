package com.broker.price.opinion.dto.DTAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationInformation {

    @JsonProperty("LegalDescription")
    public String LegalDescription;

    @JsonProperty("APN")
    public String APN;

    @JsonProperty("AlternateAPN")
    public String AlternateAPN;

    @JsonProperty("TaxAccountNumber")
    public String TaxAccountNumber;

    @JsonProperty("Subdivision")
    public String Subdivision;

    @JsonProperty("Latitude")
    public String Latitude;

    @JsonProperty("Longitude")
    public String Longitude;

    @JsonProperty("CountyFips")
    public String CountyFips;

    @JsonProperty("TownshipRangeSection")
    public String TownshipRangeSection;

    @JsonProperty("MunicipalityTownship")
    public String MunicipalityTownship;

    @JsonProperty("CensusTract")
    public String CensusTract;

    @JsonProperty("CensusBlock")
    public String CensusBlock;

    @JsonProperty("TractNumber")
    public String TractNumber;

    @JsonProperty("LegalBookPage")
    public String LegalBookPage;

    @JsonProperty("LegalLot")
    public String LegalLot;

    @JsonProperty("LegalBlock")
    public String LegalBlock;

    @JsonProperty("MapReferenceOne")
    public String MapReferenceOne;

    @JsonProperty("MapReferenceTwo")
    public String MapReferenceTwo;

    @JsonProperty("NeighborhoodName")
    public String NeighborhoodName;

    @JsonProperty("SchoolDistrict")
    public String SchoolDistrict;

    @JsonProperty("ElementarySchool")
    public String ElementarySchool;

    @JsonProperty("MiddleSchool")
    public String MiddleSchool;

    @JsonProperty("HighSchool")
    public String HighSchool;

    @JsonProperty("ElementarySchoolId")
    public String ElementarySchoolId;

    @JsonProperty("MiddleSchoolId")
    public String MiddleSchoolId;

    @JsonProperty("HighSchoolId")
    public String HighSchoolId;
}