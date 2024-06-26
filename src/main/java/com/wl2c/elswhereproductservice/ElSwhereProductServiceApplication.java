package com.wl2c.elswhereproductservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ElSwhereProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElSwhereProductServiceApplication.class, args);
    }

}
