package com.wl2c.elswhereproductservice.domain.batch;

import com.wl2c.elswhereproductservice.domain.like.service.impl.CachedLikeServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class ProductLikeDumpScheduler {

    private final CachedLikeServiceImpl service;

    @Scheduled(fixedDelayString = "${app.product.like.dump-delay}")
    public void dumpToDB() {
        if (service.dumpToDB() > 0) {
            log.info("product likes in memory dump to DB.");
        }
    }
}
