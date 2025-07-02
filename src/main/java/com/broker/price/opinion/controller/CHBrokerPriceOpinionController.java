package com.broker.price.opinion.controller;

import com.broker.price.opinion.dto.BrokerPriceOpinionPDFInfoDTO;
import com.broker.price.opinion.service.BrokerPriceOpinionPDFInfoService;
import com.broker.price.opinion.service.FillBrokerPriceOpinionPDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("bpo")
public class CHBrokerPriceOpinionController {

    @Autowired
    private FillBrokerPriceOpinionPDFService FillBrokerPriceOpinionPDFService;

    @Autowired
    private BrokerPriceOpinionPDFInfoService BrokerPriceOpinionPDFInfoService;

    @CrossOrigin
    @GetMapping("/info")
    public BrokerPriceOpinionPDFInfoDTO getBrokerPriceOpinionPDFInformation(
            @RequestParam("metro") String metro,
            @RequestParam("mls_id") String mls_id) {
        return BrokerPriceOpinionPDFInfoService.getBrokerPriceOpinionPDFInformation(metro, mls_id);
    }

    @CrossOrigin
    @PostMapping("/generate-pdf")
    public String generateBrokerPriceOpinionPDF(
            @RequestParam("report_name") String report_name,
            @RequestBody() BrokerPriceOpinionPDFInfoDTO brokerPriceOpinionPDFInfoDTO) throws IOException {
        return FillBrokerPriceOpinionPDFService.fillPlaltabPDF(report_name, brokerPriceOpinionPDFInfoDTO);
    }
}