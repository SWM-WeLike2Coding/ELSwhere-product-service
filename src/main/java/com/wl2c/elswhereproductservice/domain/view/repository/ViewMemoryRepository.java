package com.wl2c.elswhereproductservice.domain.view.repository;

import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

public interface ViewMemoryRepository {

    /**
     * 조회수 추가
     *
     * @param productId 상품 ID
     * @param userId    사용자 ID
     */
    void view(Long productId, Long userId);

    /**
     * 메모리에서 사용자가 조회한적이 있는지 확인한다.
     * 캐싱되어있는 경우에는 true/false로 반환하지만, 캐싱되어있지 않다면 null을
     * 반환한다.
     *
     * @param productId 상품 ID
     * @param userId    사용자 ID
     * @return 사용자가 조회한적이 있는지 반환. 캐싱된 데이터가 없다면 null반환.
     */
    Boolean isViewed(Long productId, Long userId);

    /**
     * 조회 여부를 메모리에 캐싱한다.
     *
     * @param productId 상품 ID
     * @param userId    사용자 ID
     */
    void setIsViewed(Long productId, Long userId);

    /**
     * 메모리에 캐싱된 조회수 확인.
     *
     * @param productId 상품 ID
     * @return 캐싱된 조회수. 없으면 -1리턴.
     */
    int getCachedViewCount(Long productId);

    /**
     * 조회수 생성
     *
     * @param productId 상품 ID
     */
    void setViewCount(Long productId, Duration expiresAfter);

    /**
     * 조회수 1 증가
     *
     * @param productId 요소 ID
     */
    void increaseViewCount(Long productId);

    /**
     * 캐싱되어 있는 상품에 대한 조회수를 내림차순으로 가져온다.
     *
     * @return 캐싱된 조회수를 내림차순으로 반환
     */
    Set<ZSetOperations.TypedTuple<String>> getCachedViewCountDesc();

    /**
     * 캐싱된 특정 날짜의 모든 일일 조회수 데이터중에서 특정 유저의 것들을 모두 삭제한다.
     */
    void deleteAllDailyViews(LocalDate localDate, Long userId);

    /**
     * 메모리에 저장된 특정 날짜의 모든 일일 조회수 데이터를 삭제한다.
     */
    void deleteAllDailyViews(LocalDate localDate);

}
