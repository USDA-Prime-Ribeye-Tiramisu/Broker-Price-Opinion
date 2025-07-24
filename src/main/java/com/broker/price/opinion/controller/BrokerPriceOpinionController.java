package com.broker.price.opinion.controller;

import com.broker.price.opinion.dto.BrokerPriceOpinionPDFInfoDTO;
import com.broker.price.opinion.dto.ComparablePropertyInformation;
import com.broker.price.opinion.service.BrokerPriceOpinionPDFInfoService;
import com.broker.price.opinion.service.FillBrokerPriceOpinionPDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @CrossOrigin
    @GetMapping("/generate-bpo-information")
    public ResponseEntity<?> generateBPOInformation(@RequestParam("property_id") String property_id) {
        Integer id = brokerPriceOpinionPDFInfoService.generateBPOInformationRequest(property_id);
        return (id == null) ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(id, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/get-bpo-information-by-id")
    public ResponseEntity<BrokerPriceOpinionPDFInfoDTO> getBPOInformationById(@RequestParam("id") Integer id) {
        try {
            BrokerPriceOpinionPDFInfoDTO dto = brokerPriceOpinionPDFInfoService.getBPOInformationById(id);
            return ResponseEntity.ok(dto);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @CrossOrigin
    @PostMapping("/update-bpo-information-by-id")
    public void updateBPOInformationById(@RequestParam("id") Integer id,
                                         @RequestBody() BrokerPriceOpinionPDFInfoDTO brokerPriceOpinionPDFInfoDTO) {
        brokerPriceOpinionPDFInfoService.updateBPOInformation(id, brokerPriceOpinionPDFInfoDTO);
    }

    @CrossOrigin
    @PostMapping("/update-bpo-information-comp-by-id")
    public void updateBPOInformationCompById(@RequestParam("id") Integer id,
                                             @RequestParam("comp_number") Integer comp_number,
                                             @RequestParam("status") String status,
                                             @RequestBody() ComparablePropertyInformation comparablePropertyInformation) {
        brokerPriceOpinionPDFInfoService.updateBPOInformationComp(id, comp_number, status, comparablePropertyInformation);
    }
}