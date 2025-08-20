package com.broker.price.opinion.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PropertyInformation {

    private Integer numberOfUnits;
    private String propertyType;
    private String propertyStyle;
    private Integer sqftGLA;
    private Integer totalRooms;
    private Integer bedrooms;
    private Double bathrooms;
    private Integer garageSpaces;
    private String garage;
    private Integer yearBuilt;
    private String view;
    private String pool;
    private String spa;
    private String featurePorch;
    private String featurePatio;
    private String featureDeck;
    private Integer numberOfFireplaces;
    private String overallCondition;
    private String occupancy;
    private Integer currentRent;
    private Integer marketRent;
    private String isListed;
    private String isListedInPast12Months;
    private Integer listPrice;
    private String nameOfListingCompany;
    private String listingAgentPhone;
    private String isTransferredInPast12Months;
    private String priorSaleDate;
    private Double priorSalePrice;
    private Double currentTax;
    private Double delinquentTax;
    private String condoOrPUD;
    private Double feeHOA;
    private String zoning;
    private Double lotSize;
    private Double landValue;
    private String isConformsToNeighborhood;

    private String construction;
    private String condition;
    private String basementAndFinish;
    private String heating;
    private String cooling;
    private String carport;
    private String additionalAmenities;
    private Integer netAdjustments;
}