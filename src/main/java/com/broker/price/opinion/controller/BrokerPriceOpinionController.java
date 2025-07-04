package com.broker.price.opinion.controller;

import com.broker.price.opinion.dto.BrokerPriceOpinionPDFInfoDTO;
import com.broker.price.opinion.service.BrokerPriceOpinionPDFInfoService;
import com.broker.price.opinion.service.FillBrokerPriceOpinionPDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("bpo")
public class BrokerPriceOpinionController {

    @Autowired
    private FillBrokerPriceOpinionPDFService fillBrokerPriceOpinionPDFService;

    @Autowired
    private BrokerPriceOpinionPDFInfoService brokerPriceOpinionPDFInfoService;

    @CrossOrigin
    @GetMapping("/info")
    public BrokerPriceOpinionPDFInfoDTO getBrokerPriceOpinionPDFInformation(
            @RequestParam("property_id") String property_id) {
        return brokerPriceOpinionPDFInfoService.getBrokerPriceOpinionPDFInformation(property_id);
    }

    @CrossOrigin
    @PostMapping("/generate-pdf")
    public String generateBrokerPriceOpinionPDF(
            @RequestBody() BrokerPriceOpinionPDFInfoDTO brokerPriceOpinionPDFInfoDTO) throws IOException {
        return fillBrokerPriceOpinionPDFService.fillPlaltabPDF(brokerPriceOpinionPDFInfoDTO);
    }
}