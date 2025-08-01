package com.broker.price.opinion.controller;

import com.broker.price.opinion.service.IVueitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("ivueit")
public class IVueitAPIController {

    @Autowired
    private IVueitService service;

    @CrossOrigin
    @GetMapping("/create-request")
    public String createRequest(
            @RequestParam("property_id") String property_id,
            @RequestParam("address") String address,
            @RequestParam("city") String city,
            @RequestParam("state") String state,
            @RequestParam("zipcode") String zipcode) {
        return service.createIVueitImagesRequest(property_id, address, city, state, zipcode);
    }

    @CrossOrigin
    @GetMapping("/get-images")
    public List<String> getImages(@RequestParam("vue_id") String vue_id) {
        return service.getIVueitImages(vue_id);
    }
}