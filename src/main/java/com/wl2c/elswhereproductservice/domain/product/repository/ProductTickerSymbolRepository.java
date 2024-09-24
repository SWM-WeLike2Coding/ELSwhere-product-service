package com.wl2c.elswhereproductservice.domain.product.repository;

import com.wl2c.elswhereproductservice.domain.product.model.entity.ProductTickerSymbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ProductTickerSymbolRepository extends JpaRepository<ProductTickerSymbol, Long> {

    @Query("select p from ProductTickerSymbol p " +
            "where p.product.id = :productId ")
    List<ProductTickerSymbol> findAllByProductId(@RequestParam("productId") Long productId);
}
