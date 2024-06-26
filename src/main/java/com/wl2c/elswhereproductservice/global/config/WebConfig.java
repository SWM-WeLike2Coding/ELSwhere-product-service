package com.wl2c.elswhereproductservice.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@RequiredArgsConstructor
public class WebConfig {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
