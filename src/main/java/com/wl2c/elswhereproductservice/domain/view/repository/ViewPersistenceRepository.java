package com.wl2c.elswhereproductservice.domain.view.repository;

import com.wl2c.elswhereproductservice.domain.view.model.entity.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ViewPersistenceRepository extends JpaRepository<View, Long> {

    @Query("select v " +
            "from View v " +
            "where v.dailyViewCount > 0 ")
    List<View> findAllByDailyViewCount();

    @Modifying
    @Query("update View v " +
            "set v.dailyViewCount = :value " +
            "where v.product.id = :productId ")
    void updateDailyProductViewCount(@Param("productId") Long productId, @Param("value") int value);

    @Modifying
    @Query("update View v " +
            "set v.totalViewCount = v.totalViewCount + :dailyViewCount, " +
                    "v.dailyViewCount = 0 " +
            "where v.product.id = :productId")
    void updateTotalViewCount(@Param("productId") Long productId, @Param("dailyViewCount") int dailyViewCount);
}
