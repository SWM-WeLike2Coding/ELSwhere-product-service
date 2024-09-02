package com.wl2c.elswhereproductservice.mock;

import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.domain.product.model.entity.ProductTickerSymbol;
import com.wl2c.elswhereproductservice.domain.product.model.entity.TickerSymbol;
import com.wl2c.elswhereproductservice.util.EntityUtil;

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

    public static ProductTickerSymbol create(Product product,
                                             TickerSymbol tickerSymbol,
                                             Long productTickerSymbolId) {
        ProductTickerSymbol productTickerSymbol = ProductTickerSymbol.builder()
                .product(product)
                .tickerSymbol(tickerSymbol)
                .build();
        EntityUtil.injectId(ProductTickerSymbol.class, productTickerSymbol, productTickerSymbolId);

        return productTickerSymbol;
    }

    public static List<ProductTickerSymbol> createList(Product product,
                                                       List<TickerSymbol> tickerSymbolList) {
        List<ProductTickerSymbol> productTickerSymbolList = new ArrayList<>();

        for (TickerSymbol tickerSymbol : tickerSymbolList) {
            productTickerSymbolList.add(create(product, tickerSymbol));
        }

        return productTickerSymbolList;
    }

    public static List<ProductTickerSymbol> createList(Product product,
                                                       List<TickerSymbol> tickerSymbolList,
                                                       List<Long> productTickerSymbolIdList) {
        List<ProductTickerSymbol> productTickerSymbolList = new ArrayList<>();

        for (int i = 0; i < tickerSymbolList.size(); i++) {
            productTickerSymbolList.add(create(product, tickerSymbolList.get(i), productTickerSymbolIdList.get(i)));
        }

        return productTickerSymbolList;
    }
}
