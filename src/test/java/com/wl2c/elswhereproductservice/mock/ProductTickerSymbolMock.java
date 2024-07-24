package com.wl2c.elswhereproductservice.mock;

import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.domain.product.model.entity.ProductTickerSymbol;
import com.wl2c.elswhereproductservice.domain.product.model.entity.TickerSymbol;

import java.util.ArrayList;
import java.util.List;

public class ProductTickerSymbolMock {
    public static ProductTickerSymbol create(Product product,
                                                 TickerSymbol tickerSymbol) {
        return ProductTickerSymbol.builder()
                .product(product)
                .tickerSymbol(tickerSymbol)
                .build();
    }

    public static List<ProductTickerSymbol> createList(Product product,
                                                       List<TickerSymbol> TickerSymbolList) {
        List<ProductTickerSymbol> productTickerSymbolList = new ArrayList<>();

        for (TickerSymbol tickerSymbol : TickerSymbolList) {
            productTickerSymbolList.add(create(product, tickerSymbol));
        }

        return productTickerSymbolList;
    }
}
