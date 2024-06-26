package com.wl2c.elswhereproductservice.product.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health Check", description = "서버 health 관련 api")
@RestController
@RequiredArgsConstructor
public class HealthController {

    @Value("${spring.application.name}")
    private String service;

    @GetMapping("/health_check")
    public String status() {
        return "It's Working in " + service;
    }
}
