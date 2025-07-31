package com.broker.price.opinion.controller;

import com.broker.price.opinion.service.BrokerPriceOpinionPDFInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    private BrokerPriceOpinionPDFInfoService service;

    @CrossOrigin
    @GetMapping()
    public void test() {
        // service.generateBPOInformation(19, "245558635");
        // service.generateBPOInformation(20, "245560024");
    }
}