package com.wl2c.elswhereproductservice.domain.product.service;

import com.wl2c.elswhereproductservice.domain.product.exception.NotOnSaleProductException;
import com.wl2c.elswhereproductservice.domain.product.exception.ProductNotFoundException;
import com.wl2c.elswhereproductservice.domain.product.exception.WrongProductSortTypeException;
import com.wl2c.elswhereproductservice.domain.product.model.dto.list.SummarizedProductDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.request.RequestProductSearchDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.response.ResponseProductComparisonTargetDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.response.ResponseSingleProductDto;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.domain.product.model.entity.TickerSymbol;
import com.wl2c.elswhereproductservice.domain.product.repository.ProductRepository;
import com.wl2c.elswhereproductservice.domain.product.repository.ProductSearchRepository;
import com.wl2c.elswhereproductservice.domain.product.repository.TickerSymbolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final TickerSymbolRepository tickerSymbolRepository;
    private final ProductSearchRepository productSearchRepository;

    public Page<SummarizedProductDto> listByOnSale(String type, Pageable pageable) {
        Sort sort = switch (type) {
            case "latest" -> Sort.by(Sort.Order.desc("subscriptionStartDate"), Sort.Order.desc("lastModifiedAt"));
            case "knock-in" -> Sort.by(Sort.Order.asc("knockIn").nullsLast(), Sort.Order.desc("lastModifiedAt"));
            case "profit" -> Sort.by(Sort.Order.desc("yieldIfConditionsMet"), Sort.Order.desc("lastModifiedAt"));
            default -> null;
        };

        if (sort == null) {
            throw new WrongProductSortTypeException();
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<Product> products = productRepository.listByOnSale(type, sortedPageable);

        return products.map(SummarizedProductDto::new);
    }

    public Page<SummarizedProductDto> listByEndSale(String type, Pageable pageable) {
        Sort sort = switch (type) {
            case "latest" -> Sort.by(Sort.Order.desc("subscriptionStartDate"), Sort.Order.desc("lastModifiedAt"));
            case "knock-in" -> Sort.by(Sort.Order.asc("knockIn").nullsLast(), Sort.Order.desc("lastModifiedAt"));
            case "profit" -> Sort.by(Sort.Order.desc("yieldIfConditionsMet"), Sort.Order.desc("lastModifiedAt"));
            default -> null;
        };

        if (sort == null) {
            throw new WrongProductSortTypeException();
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<Product> products = productRepository.listByEndSale(type, sortedPageable);

        return products.map(SummarizedProductDto::new);
    }

    public List<SummarizedProductDto> listByProductIds(List<Long> productIdList) {
        List<Product> productList = productRepository.listByIds(productIdList);

        return productList.stream()
                .map(SummarizedProductDto::new)
                .collect(Collectors.toList());
    }

    public ResponseSingleProductDto findOne(Long id) {
        Product product = productRepository.findOne(id).orElseThrow(ProductNotFoundException::new);
        List<TickerSymbol> tickerSymbolList = tickerSymbolRepository.findTickerSymbolList(id);

        Map<String, String> equityTickerSymbols = tickerSymbolList.stream()
                .collect(Collectors.toMap(TickerSymbol::getEquityName, TickerSymbol::getTickerSymbol));

        return new ResponseSingleProductDto(product, equityTickerSymbols);
    }

    public Map<String, List<ResponseProductComparisonTargetDto>> findComparisonTargets(Long id) {
        Map<String, List<ResponseProductComparisonTargetDto>> result = new HashMap<>();

        Product product = productRepository.isItProductOnSale(id).orElseThrow(NotOnSaleProductException::new);
        List<TickerSymbol> tickerSymbolEntityList = tickerSymbolRepository.findTickerSymbolList(id);
        List<String> tickerSymbolList = tickerSymbolEntityList.stream()
                .map(TickerSymbol::getTickerSymbol)
                .toList();
        List<ResponseProductComparisonTargetDto> target = new ArrayList<>();
        target.add(new ResponseProductComparisonTargetDto(product));

        List<Product> productComparisonResults = productRepository.findComparisonResults(id, product.getEquityCount(), tickerSymbolList);
        List<ResponseProductComparisonTargetDto> comparisonResults = productComparisonResults.stream()
                                        .map(ResponseProductComparisonTargetDto::new)
                                        .toList();

        result.put("target", target);
        result.put("results", comparisonResults);

        return result;
    }

    public Page<SummarizedProductDto> searchProduct(RequestProductSearchDto requestProductSearchDto,
                                                    Pageable pageable) {
        return productSearchRepository.search(requestProductSearchDto, pageable);
    }
}
