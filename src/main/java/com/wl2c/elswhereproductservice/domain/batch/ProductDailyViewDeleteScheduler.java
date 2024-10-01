package com.wl2c.elswhereproductservice.domain.batch;

import com.wl2c.elswhereproductservice.domain.view.model.entity.View;
import com.wl2c.elswhereproductservice.domain.view.repository.ViewMemoryRepository;
import com.wl2c.elswhereproductservice.domain.view.repository.ViewPersistenceRepository;
import com.wl2c.elswhereproductservice.domain.view.service.impl.CachedViewServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class ProductDailyViewDeleteScheduler {

    private final CachedViewServiceImpl service;

    private final ViewPersistenceRepository viewPersistenceRepository;
    private final ViewMemoryRepository viewMemoryRepository;

    @Scheduled(cron = "${app.product.view.cron}")
    @Transactional
    public void updateDailyViewAndDeleteCache() {
        service.dumpToDB();

        List<View> viewList = viewPersistenceRepository.findAllByDailyViewCount();
        for (View v : viewList) {
            viewPersistenceRepository.updateTotalViewCount(v.getId(), v.getDailyViewCount());
        }

        LocalDate today = LocalDate.now();
        viewMemoryRepository.deleteAllDailyViews(today.minusDays(1));
    }
}
