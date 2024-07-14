package com.wl2c.elswhereproductservice.domain.product.repository;

import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p " +
            "where p.productState = 'ACTIVE' and p.subscriptionEndDate >= CURRENT_DATE " +
            "order by case when :sortType = 'knock-in' and p.knockIn is null then 1 else 0 end ")
    Page<Product> listByOnSale(@Param("sortType") String sortType, Pageable pageable);

    @Query("select p from Product p " +
            "where p.productState = 'ACTIVE' and p.subscriptionEndDate < CURRENT_DATE " +
            "order by case when :sortType = 'knock-in' and p.knockIn is null then 1 else 0 end ")
    Page<Product> listByEndSale(Pageable pageable);

    @Query("select p from Product p where p.productState = 'ACTIVE' and p.id = :id ")
    Optional<Product> findOne(@Param("id") Long id);
}
