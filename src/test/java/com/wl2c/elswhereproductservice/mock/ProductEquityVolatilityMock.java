package com.wl2c.elswhereproductservice.mock;

import com.wl2c.elswhereproductservice.domain.product.model.entity.ProductEquityVolatility;
import com.wl2c.elswhereproductservice.domain.product.model.entity.ProductTickerSymbol;
import com.wl2c.elswhereproductservice.util.EntityUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductEquityVolatilityMock {

    public static ProductEquityVolatility create(ProductTickerSymbol productTickerSymbol,
                                                 BigDecimal volatility) {
        return ProductEquityVolatility.builder()
                .productTickerSymbol(productTickerSymbol)
                .volatility(volatility)
                .build();
    }

    public static ProductEquityVolatility create(ProductTickerSymbol productTickerSymbol,
                                                 BigDecimal volatility,
                                                 Long productEquityVolatilityId) {
        ProductEquityVolatility productEquityVolatility = ProductEquityVolatility.builder()
                .productTickerSymbol(productTickerSymbol)
                .volatility(volatility)
                .build();
        EntityUtil.injectId(ProductEquityVolatility.class, productEquityVolatility, productEquityVolatilityId);

        return productEquityVolatility;
    }

    public static List<ProductEquityVolatility> createList(List<ProductTickerSymbol> productTickerSymbolList,
                                                           List<BigDecimal> volatilityList) {
        List<ProductEquityVolatility> productEquityVolatilityList = new ArrayList<>();

        for (int i = 0; i < productTickerSymbolList.size(); i++) {
            productEquityVolatilityList.add(create(productTickerSymbolList.get(i), volatilityList.get(i)));
        }

        return productEquityVolatilityList;
    }

    public static List<ProductEquityVolatility> createList(List<ProductTickerSymbol> productTickerSymbolList,
                                                           List<BigDecimal> volatilityList,
                                                           List<Long> productEquityVolatilityIdList) {
        List<ProductEquityVolatility> productEquityVolatilityList = new ArrayList<>();

        for (int i = 0; i < productTickerSymbolList.size(); i++) {
            productEquityVolatilityList.add(create(productTickerSymbolList.get(i), volatilityList.get(i), productEquityVolatilityIdList.get(i)));
        }

        return productEquityVolatilityList;
    }
}
