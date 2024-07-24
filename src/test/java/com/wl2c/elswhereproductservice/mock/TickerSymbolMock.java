package com.wl2c.elswhereproductservice.mock;

import com.wl2c.elswhereproductservice.domain.product.model.UnderlyingAssetType;
import com.wl2c.elswhereproductservice.domain.product.model.entity.TickerSymbol;

public class TickerSymbolMock {
    public static TickerSymbol create(String tickerSymbol,
                                      String equityName,
                                      UnderlyingAssetType underlyingAssetType) {
        return TickerSymbol.builder()
                .tickerSymbol(tickerSymbol)
                .equityName(equityName)
                .underlyingAssetType(underlyingAssetType)
                .build();
    }
}
