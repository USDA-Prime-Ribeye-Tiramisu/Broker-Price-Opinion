package com.broker.price.opinion.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NeighborhoodInformation {

    private String marketConditions;
    private Integer numberOfCompetitiveListings;
    private Integer priceRangeOfCurrentListingAndSalesFrom;
    private Integer priceRangeOfCurrentListingAndSalesTo;
    private String supplyAndDemand;
    private String positiveOrNegativeInfluences;
    private String location;
    private String neighborhoodTrend;
    private String homesInNeighborhoodAre;
    private String averageMarketTime;
    private String mostProbableBuyer;
}