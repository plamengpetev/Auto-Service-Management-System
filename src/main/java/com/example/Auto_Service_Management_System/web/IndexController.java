package com.example.Auto_Service_Management_System.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;

@Configuration
public class IndexController {

    @GetMapping("/")
    public String getIndexController() {
        return "index";
    }

}
