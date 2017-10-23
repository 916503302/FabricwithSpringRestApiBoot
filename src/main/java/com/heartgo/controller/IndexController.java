package com.heartgo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @GetMapping("/get")
    public String index() {
        return "ajax";
    }

    @GetMapping("/getTest")
    public String yy() {
        return "PingAnTest";
    }
    @GetMapping("/getTransaction")
    public String Transaction() {
        return "Transaction";
    }
}