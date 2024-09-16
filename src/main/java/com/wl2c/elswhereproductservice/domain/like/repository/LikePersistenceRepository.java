package com.wl2c.elswhereproductservice.domain.like.repository;

import com.wl2c.elswhereproductservice.domain.like.model.entity.LikeElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

public interface LikePersistenceRepository extends JpaRepository<LikeElement, Long> {
    @Query("select l.count " +
            "from LikeElement l " +
            "where l.product.id = :productId")
    int findLikeCountByProductId(@Param("productId") Long productId);

    @Modifying
    @Query("update LikeElement l " +
            "set l.count = l.count + :value " +
            "where l.product.id = :productId ")
    void updateProductLikeCount(@Param("productId") Long productId, @Param("value") int value);
}
