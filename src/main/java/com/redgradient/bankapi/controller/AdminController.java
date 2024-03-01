package com.redgradient.bankapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin")
public class AdminController {
    @GetMapping
    public void createUser() {
        System.out.println("--- createUser ---");
    }
}
