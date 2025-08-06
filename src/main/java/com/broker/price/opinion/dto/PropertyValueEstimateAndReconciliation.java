package com.broker.price.opinion.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PropertyValueEstimateAndReconciliation {

    private String averageMarketTime;
    private Integer asIs;
    private Integer asIsListPrice;
    private Integer asRepaired;
    private Integer asRepairedListPrice;
    private Integer asIsQuickSale090Days;
    private Integer asIsList090Days;
    private Integer asRepaired090Days;
    private Integer asRepairedList090Days;
    private String brokerORRealtorName;
    private String licenseState;
    private String licenseNumber;
}