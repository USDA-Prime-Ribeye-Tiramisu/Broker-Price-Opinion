package com.broker.price.opinion.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("test")
public class TestController {

    @CrossOrigin
    @GetMapping()
    public void test() {

    }
}