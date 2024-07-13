package com.wl2c.elswhereproductservice.domain.product.service;

import com.wl2c.elswhereproductservice.domain.product.exception.ProductNotFoundException;
import com.wl2c.elswhereproductservice.domain.product.model.dto.response.ResponseSingleProductDto;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.domain.product.model.entity.TickerSymbol;
import com.wl2c.elswhereproductservice.domain.product.repository.ProductRepository;
import com.wl2c.elswhereproductservice.domain.product.repository.TickerSymbolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final TickerSymbolRepository tickerSymbolRepository;

    public ResponseSingleProductDto findOne(Long id) {
        Product product = productRepository.findOne(id).orElseThrow(ProductNotFoundException::new);
        List<TickerSymbol> tickerSymbolList = tickerSymbolRepository.findTickerSymbolList(id);

        Map<String, String> equityTickerSymbols = tickerSymbolList.stream()
                .collect(Collectors.toMap(TickerSymbol::getEquityName, TickerSymbol::getTickerSymbol));

        return new ResponseSingleProductDto(product, equityTickerSymbols);
    }
}
