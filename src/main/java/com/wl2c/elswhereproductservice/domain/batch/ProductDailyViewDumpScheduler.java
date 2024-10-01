package com.wl2c.elswhereproductservice.domain.batch;

import com.wl2c.elswhereproductservice.domain.view.service.impl.CachedViewServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class ProductDailyViewDumpScheduler {

    private final CachedViewServiceImpl service;

    @Scheduled(fixedDelayString = "${app.product.view.dump-delay}")
    public void dumpToDB() {
        if (service.dumpToDB() > 0) {
            log.info("product daily views in memory dump to DB");
        }
    }
}
