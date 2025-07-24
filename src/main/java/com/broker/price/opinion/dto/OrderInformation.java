package com.broker.price.opinion.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderInformation {

    private String loanNumber;
    private String client;
    private String orderFor;
    private String orderNumber;
    private String borrowerOrOwnerName;
    private String address;
    private String city;
    private String state;
    private String zipcode;
    private String county;
    private String parcelID;
    private String feeSimpleORLeasehold;
}