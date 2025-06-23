package com.broker.price.opinion.controller;

import com.broker.price.opinion.service.FillBrokerPriceOpinionPDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("fill-price-opinion-pdf")
public class FillBrokerPriceOpinionPDFController {

    @Autowired
    private FillBrokerPriceOpinionPDFService fillBrokerPriceOpinionPDFService;

    @CrossOrigin
    @GetMapping()
    public void fillBrokerPriceOpinionPDF() throws IOException {
        fillBrokerPriceOpinionPDFService.fillPlaltabPDF();
    }
}