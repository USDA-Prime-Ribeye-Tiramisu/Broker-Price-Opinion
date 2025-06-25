package com.broker.price.opinion.controller;

import com.broker.price.opinion.dto.BrokerPriceOpinionFile;
import com.broker.price.opinion.dto.BrokerPriceOpinionPDFInfoDTO;
import com.broker.price.opinion.service.BrokerPriceOpinionPDFInfoService;
import com.broker.price.opinion.service.BrokerPriceOpinionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("bpo")
public class BrokerPriceOpinionController {

    @Autowired
    private BrokerPriceOpinionService brokerPriceOpinionService;

    @Autowired
    private BrokerPriceOpinionPDFInfoService brokerPriceOpinionPDFInfoService;

    @CrossOrigin
    @GetMapping("/file")
    public Optional<BrokerPriceOpinionFile> getBrokerPriceOpinionFile(
            @RequestParam Long id) {
        return brokerPriceOpinionService.findBrokerPriceOpinion(id);
    }

    @CrossOrigin
    @PostMapping()
    public ResponseEntity<?> generateBrokerPriceOpinionPDF(
            @RequestParam("report_name") String report_name,
            @RequestParam("metro") String metro,
            @RequestParam("mls_id") String mls_id) {

        BrokerPriceOpinionFile file = brokerPriceOpinionService.generateBrokerPriceOpinionPDFRequest(report_name, metro, mls_id);

        if (file == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(file, HttpStatus.OK);
        }
    }

    @CrossOrigin
    @GetMapping("/info")
    public BrokerPriceOpinionPDFInfoDTO getBrokerPriceOpinionPDFInformation(
            @RequestParam("metro") String metro,
            @RequestParam("mls_id") String mls_id) {
        return brokerPriceOpinionPDFInfoService.getBrokerPriceOpinionPDFInformation(metro, mls_id);
    }
}