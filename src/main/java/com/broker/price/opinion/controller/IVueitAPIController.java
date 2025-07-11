package com.broker.price.opinion.controller;

import com.broker.price.opinion.service.IVueitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("ivueit")
public class IVueitAPIController {

    @Autowired
    private IVueitService service;
}