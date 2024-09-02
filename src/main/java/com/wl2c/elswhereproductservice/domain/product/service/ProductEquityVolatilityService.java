package com.wl2c.elswhereproductservice.domain.product.service;

import com.wl2c.elswhereproductservice.domain.product.exception.ProductNotFoundException;
import com.wl2c.elswhereproductservice.domain.product.model.dto.response.ResponseProductEquityVolatilityDto;
import com.wl2c.elswhereproductservice.domain.product.model.entity.ProductEquityVolatility;
import com.wl2c.elswhereproductservice.domain.product.model.entity.ProductTickerSymbol;
import com.wl2c.elswhereproductservice.domain.product.repository.ProductEquityVolatilityRepository;
import com.wl2c.elswhereproductservice.domain.product.repository.ProductRepository;
import com.wl2c.elswhereproductservice.domain.product.repository.ProductTickerSymbolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEquityVolatilityService {

    private final ProductEquityVolatilityRepository productEquityVolatilityRepository;
    private final ProductTickerSymbolRepository productTickerSymbolRepository;
    private final ProductRepository productRepository;

    public ResponseProductEquityVolatilityDto findProductEquityVolatilities(Long productId) {
        if (productRepository.findOne(productId).isEmpty()) {
            throw new ProductNotFoundException();
        }

        List<ProductTickerSymbol> productTickerSymbolList = productTickerSymbolRepository.findAllByProductId(productId);
        List<Long> productTickerSymbolIdList = productTickerSymbolList.stream()
                .map(ProductTickerSymbol::getId)
                .toList();
        List<String> equityList = productTickerSymbolList.stream()
                .map(ProductTickerSymbol::getEquityName)
                .toList();

        List<ProductEquityVolatility> productEquityVolatilityList = productEquityVolatilityRepository.findAllByProductTickerSymbol(productTickerSymbolIdList);

        Map<String, BigDecimal> result = new HashMap<>();
        for (ProductTickerSymbol productTickerSymbol : productTickerSymbolList) {
            for (ProductEquityVolatility productEquityVolatility : productEquityVolatilityList) {
                if (productEquityVolatility.getId().equals(productTickerSymbol.getId()))
                    result.put(productTickerSymbol.getEquityName(), productEquityVolatility.getVolatility());
            }
        }

        return new ResponseProductEquityVolatilityDto(equityList, result);
    }
}
