package com.wl2c.elswhereproductservice.domain.product.repository;

import com.wl2c.elswhereproductservice.domain.product.model.entity.TickerSymbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TickerSymbolRepository extends JpaRepository<TickerSymbol, Long> {
    @Query("select ts from TickerSymbol ts " +
            "inner join ProductTickerSymbol pts " +
            "on ts.id = pts.tickerSymbol.id " +
            "where pts.product.id = :productId and pts.product.productState = 'ACTIVE' ")
    List<TickerSymbol> findTickerSymbolList(@Param("productId") Long productId);

    @Override
    @Query("select ts from TickerSymbol ts " +
            "where ts.tickerSymbol <> 'NEED_TO_CHECK' ")
    List<TickerSymbol> findAll();
}
