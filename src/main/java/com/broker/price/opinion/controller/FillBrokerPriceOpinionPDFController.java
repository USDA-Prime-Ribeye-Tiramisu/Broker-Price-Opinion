package com.broker.price.opinion.controller;

import com.broker.price.opinion.service.FillBrokerPriceOpinionPDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("fill-broker-price-opinion-pdf")
public class FillBrokerPriceOpinionPDFController {

    @Autowired
    private FillBrokerPriceOpinionPDFService fillBrokerPriceOpinionPDFService;

    @CrossOrigin
    @GetMapping()
    public void fillBrokerPriceOpinionPDF(@RequestParam String full_address) throws IOException {
        fillBrokerPriceOpinionPDFService.fillPlaltabPDF(full_address);
    }
}