package com.wl2c.elswhereproductservice.domain.view.service;

public interface ViewService {

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
     * @param productId 요소 ID
     * @param userId    사용자 ID
     * @return 사용자가 조회한적이 있는지 반환. 캐싱된 데이터가 없다면 null반환.
     */
    Boolean isViewed(Long productId, Long userId);

}
