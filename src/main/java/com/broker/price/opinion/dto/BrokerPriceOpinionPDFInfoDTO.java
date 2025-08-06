package com.broker.price.opinion.dto;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BrokerPriceOpinionPDFInfoDTO {

    private String fullAddress;

    private String propertyID;

    private String status;

    private String longitude;
    private String latitude;

    private String placekey;

    private OrderInformation orderInformation;
    private PropertyInformation propertyInformation;
    private ConditionInformation conditionInformation;
    private NeighborhoodInformation neighborhoodInformation;
    private List<ComparablePropertyInformation> activeComparablePropertyInformationList;
    private List<ComparablePropertyInformation> closedComparablePropertyInformationList;
    private CommentsMade commentsMade;
    private PropertyValueEstimateAndReconciliation propertyValueEstimateAndReconciliation;

    private Integer estimatedValue;

    private String inspectionDate;

    private String additionalCommentsAddendum;

    private ImagesLinks imagesLinks;
}