package com.broker.price.opinion.controller;

import com.broker.price.opinion.dto.BrokerPriceOpinionPDFInfoDTO;
import com.broker.price.opinion.service.BrokerPriceOpinionPDFInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("broker-price-opinion-pdf-info")
public class BrokerPriceOpinionPDFInfoController {

    @Autowired
    private BrokerPriceOpinionPDFInfoService brokerPriceOpinionPDFInfoService;

    @CrossOrigin
    @GetMapping()
    public BrokerPriceOpinionPDFInfoDTO getBrokerPriceOpinionPDFInformation(@RequestParam String full_address) {
        return brokerPriceOpinionPDFInfoService.getBrokerPriceOpinionPDFInformation(full_address);
    }
}