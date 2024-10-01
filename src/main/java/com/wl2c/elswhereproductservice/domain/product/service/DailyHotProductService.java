package com.wl2c.elswhereproductservice.domain.product.service;

import com.wl2c.elswhereproductservice.domain.like.repository.LikeMemoryRepository;
import com.wl2c.elswhereproductservice.domain.product.model.dto.response.ResponseSingleProductDto;
import com.wl2c.elswhereproductservice.domain.view.repository.ViewMemoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyHotProductService {

    private final ViewMemoryRepository viewMemoryRepository;
    private final LikeMemoryRepository likeMemoryRepository;

    @Value("${app.product.hot.like-ratio}")
    private Double likeRatio;

    @Value("${app.product.hot.view-ratio}")
    private Double viewRatio;

    public List<Long> getDailyTop5Products() {

        Set<ZSetOperations.TypedTuple<String>> views = viewMemoryRepository.getCachedViewCountDesc();
        Set<ZSetOperations.TypedTuple<String>> likes = likeMemoryRepository.getCachedLikeCountDeltaDesc();

        // 가중치를 부여한 두 값을 합산하여 정렬
        Map<String, Double> productScores = new HashMap<>();
        for (ZSetOperations.TypedTuple<String> view : views) {
            productScores.put(view.getValue(), view.getScore() * viewRatio);
        }
        for (ZSetOperations.TypedTuple<String> like : likes) {
            // 좋아요 증감이 음수가 아닌 값들로만
            if (like.getScore() != null && like.getScore() > 0) {
                productScores.merge(like.getValue(), like.getScore() * likeRatio, Double::sum);
            }
        }

        return productScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .map(entry -> parseLong(entry.getKey()))
                .collect(Collectors.toList());

    }
}
