package com.broker.price.opinion.controller;

import com.broker.price.opinion.service.FoxyAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    private FoxyAIService service;

    @CrossOrigin
    @GetMapping()
    public void test() {
        // service.createImageGroup(0, "TEST 3884 Colita Loop, Livingston, TX 77351");
        service.uploadImagesBatchUserGroup(1, "e6ea8007-b55b-4167-9a0a-521ab6445558", new ArrayList<>());
    }
}