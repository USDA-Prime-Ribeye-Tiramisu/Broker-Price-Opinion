package com.broker.price.opinion.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ComparablePropertyInformation {

    private Integer compNumber;
    private String status;
    private String address;
    private String city;
    private String state;
    private String zipcode;
    private String county;
    private Double proximity;
    private Integer salePrice;
    private Double pricePerSqFt;
    private Integer originalListingPrice;
    private Integer currentListingPrice;
    private String saleDate;
    private String listDate;
    private Integer daysOnMarket;
    private String mlsID;
    private String financing;
    private String salesConcession;
    private String bankOrREOSale;
    private String location;
    private String siteOrView;
    private Double siteOrLotSize;
    private Integer yearBuilt;
    private String construction;
    private String condition;
    private String style;
    private Integer totalRooms;
    private Integer bedrooms;
    private Double bathrooms;
    private Integer grossLivingArea;
    private String basementAndFinish;
    private String heating;
    private String cooling;
    private String garage;
    private String carport;
    private String additionalAmenities;
    private Integer netAdjustments;
}