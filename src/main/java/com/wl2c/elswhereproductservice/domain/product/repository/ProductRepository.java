package com.wl2c.elswhereproductservice.domain.product.repository;

import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p " +
            "where p.productState = 'ACTIVE' and p.subscriptionEndDate >= CURRENT_DATE " +
            "order by case when :sortType = 'knock-in' and p.knockIn is null then 1 else 0 end ")
    Page<Product> listByOnSale(@Param("sortType") String sortType, Pageable pageable);

    @Query("select p from Product p where p.productState = 'ACTIVE' and p.id = :id and p.subscriptionEndDate >= CURRENT_DATE ")
    Optional<Product> isItProductOnSale(@Param("id") Long id);

    @Query("select p from Product p " +
            "where p.productState = 'ACTIVE' and p.subscriptionEndDate < CURRENT_DATE " +
            "order by case when :sortType = 'knock-in' and p.knockIn is null then 1 else 0 end ")
    Page<Product> listByEndSale(@Param("sortType") String sortType, Pageable pageable);

    @Query("select p from Product p " +
            "where p.productState = 'ACTIVE' " +
            "and p.id in :productIdList ")
    List<Product> listByIds(@Param("productIdList") List<Long> productIdList);

    @Query("select p from Product p " +
            "where p.productState = 'ACTIVE' and DATE(p.createdAt) = CURRENT_DATE ")
    List<Product> listByCreatedAtToday();

    @Query("select p from Product p where p.productState = 'ACTIVE' and p.id = :id ")
    Optional<Product> findOne(@Param("id") Long id);

    @Query("select p from Product p " +
            "where p.productState = 'ACTIVE' " +
            "and p.subscriptionEndDate >= CURRENT_DATE " +
            "and p.id <> :targetId " +
            "and p.equityCount = :targetEquityCount " +
            "and (select count(subpts.id) from ProductTickerSymbol subpts " +
            "      where subpts.product.id = p.id " +
            "      and subpts.tickerSymbol.tickerSymbol in :targetEquityTickerSymbols) = :targetEquityCount")
    List<Product> findComparisonResults(@Param("targetId") Long targetId,
                                       @Param("targetEquityCount") int targetEquityCount,
                                       List<String> targetEquityTickerSymbols);
}
