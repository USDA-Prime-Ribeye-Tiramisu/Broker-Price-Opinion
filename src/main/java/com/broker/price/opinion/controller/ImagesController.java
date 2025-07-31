package com.broker.price.opinion.controller;

import com.broker.price.opinion.service.ImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("images")
public class ImagesController {

    @Autowired
    private ImagesService service;

    @CrossOrigin
    @GetMapping("/get-all-images")
    public List<String> getAllImages(
            @RequestParam("mls_id") String mls_id, @RequestParam("display_mls_number") String display_mls_number) {
        return service.imageURLsPlatlabAWSS3(mls_id, display_mls_number);
    }

    @CrossOrigin
    @GetMapping("/get-first-image")
    public String getFirstImage(
            @RequestParam("mls_id") String mls_id, @RequestParam("display_mls_number") String display_mls_number) {
        return service.getFirstImagePlatlabTrinoServerANDPlatlabAWSS3(mls_id, display_mls_number);
    }
}