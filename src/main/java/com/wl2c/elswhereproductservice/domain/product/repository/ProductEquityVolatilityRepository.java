package com.wl2c.elswhereproductservice.domain.product.repository;

import com.wl2c.elswhereproductservice.domain.product.model.entity.ProductEquityVolatility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ProductEquityVolatilityRepository extends JpaRepository<ProductEquityVolatility, Long> {

    @Query("select p from ProductEquityVolatility p " +
            "where p.id in :productTickerSymbolIdList ")
    List<ProductEquityVolatility> findAllByProductTickerSymbol(@RequestParam("productTickerSymbolIdList") List<Long> productTickerSymbolIdList);
}
