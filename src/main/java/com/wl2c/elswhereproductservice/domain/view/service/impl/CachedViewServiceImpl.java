package com.wl2c.elswhereproductservice.domain.view.service.impl;

import com.wl2c.elswhereproductservice.domain.product.exception.ProductNotFoundException;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.domain.product.repository.ProductRepository;
import com.wl2c.elswhereproductservice.domain.view.model.entity.View;
import com.wl2c.elswhereproductservice.domain.view.repository.ViewMemoryRepository;
import com.wl2c.elswhereproductservice.domain.view.repository.ViewPersistenceRepository;
import com.wl2c.elswhereproductservice.domain.view.service.ViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CachedViewServiceImpl implements ViewService {

    private final ViewMemoryRepository viewMemoryRepository;
    private final ViewPersistenceRepository viewPersistenceRepository;
    private final ProductRepository productRepository;

    @Value("${app.product.view.count-cache-time}")
    private Duration countCacheTime;

    @Override
    @Transactional(readOnly = true)
    public void view(Long productId, Long userId) {
        int count = viewMemoryRepository.getCachedViewCount(productId);
        if (count == -1) {
            viewMemoryRepository.setViewCount(productId, countCacheTime);
        }

        if (!isViewed(productId, userId)) {
            viewMemoryRepository.view(productId, userId);
            viewMemoryRepository.increaseViewCount(productId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isViewed(Long productId, Long userId) {
        Boolean viewed = viewMemoryRepository.isViewed(productId, userId);
        if (viewed == null) {
            return false;
        }
        return viewed;
    }

    @Transactional
    public long dumpToDB() {
        Set<ZSetOperations.TypedTuple<String>> views = viewMemoryRepository.getCachedViewCountDesc();

        for (ZSetOperations.TypedTuple<String> view : views) {
            Long productId = Long.valueOf(view.getValue());
            int viewCount = (int)Math.round(view.getScore());

            Optional<View> existingProduct = viewPersistenceRepository.findById(productId);
            if (existingProduct.isPresent()) {
                viewPersistenceRepository.updateDailyProductViewCount(productId, viewCount);
            } else {
                Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);

                View newView = View.builder()
                        .product(product)
                        .totalViewCount(0L)
                        .dailyViewCount(viewCount)
                        .build();
                viewPersistenceRepository.save(newView);
            }
        }

        return views.size();
    }

}
