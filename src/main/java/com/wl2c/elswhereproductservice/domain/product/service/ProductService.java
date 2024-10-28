package com.wl2c.elswhereproductservice.domain.product.service;

import com.wl2c.elswhereproductservice.client.analysis.api.AnalysisServiceClient;
import com.wl2c.elswhereproductservice.client.analysis.dto.response.ResponseAIResultDto;
import com.wl2c.elswhereproductservice.domain.like.service.LikeService;
import com.wl2c.elswhereproductservice.domain.product.exception.NotOnSaleProductException;
import com.wl2c.elswhereproductservice.domain.product.exception.ProductNotFoundException;
import com.wl2c.elswhereproductservice.domain.product.exception.TodayReceivedProductsNotFoundException;
import com.wl2c.elswhereproductservice.domain.product.exception.WrongProductSortTypeException;
import com.wl2c.elswhereproductservice.domain.product.model.ProductType;
import com.wl2c.elswhereproductservice.domain.product.model.dto.list.SummarizedOnSaleProductDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.list.SummarizedProductDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.list.SummarizedProductForHoldingDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.request.RequestProductIdListDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.request.RequestProductSearchDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.response.ResponseProductComparisonTargetDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.response.ResponseSingleProductDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.response.ResponseTodayReceivedProductIdsDto;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.domain.product.model.entity.TickerSymbol;
import com.wl2c.elswhereproductservice.domain.product.repository.ProductRepository;
import com.wl2c.elswhereproductservice.domain.product.repository.ProductSearchRepository;
import com.wl2c.elswhereproductservice.domain.product.repository.TickerSymbolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final AnalysisServiceClient analysisServiceClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    private final ProductRepository productRepository;
    private final TickerSymbolRepository tickerSymbolRepository;
    private final ProductSearchRepository productSearchRepository;

    private final RepaymentEvaluationDatesService repaymentEvaluationDatesService;
    private final LikeService likeService;
    private final DailyHotProductService dailyHotProductService;

    public Page<SummarizedOnSaleProductDto> listByOnSale(String type, Pageable pageable) {
        Sort sort = switch (type) {
            case "latest" -> Sort.by(Sort.Order.desc("subscriptionStartDate"), Sort.Order.desc("lastModifiedAt"));
            case "knock-in" -> Sort.by(Sort.Order.asc("knockIn").nullsLast(), Sort.Order.desc("lastModifiedAt"));
            case "profit" -> Sort.by(Sort.Order.desc("yieldIfConditionsMet"), Sort.Order.desc("lastModifiedAt"));
            case "deadline" -> Sort.by(Sort.Order.asc("subscriptionEndDate"), Sort.Order.desc("lastModifiedAt"));
            default -> null;
        };

        if (sort == null) {
            throw new WrongProductSortTypeException();
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<Product> products = productRepository.listByOnSale(type, sortedPageable);

        List<Long> stepDownProductIds = products.getContent().stream()
                .filter(product -> product.getType() == ProductType.STEP_DOWN) // STEP_DOWN 타입 필터링
                .map(Product::getId)
                .toList();
        List<ResponseAIResultDto> responseStepDownAIResultDtos = listStepDownAIResult(stepDownProductIds);

        // AI 결과 리스트에서 productId를 key로 하는 Map으로 변환
        Map<Long, BigDecimal> productSafetyScoreMap = responseStepDownAIResultDtos.stream()
                .collect(Collectors.toMap(ResponseAIResultDto::getProductId, ResponseAIResultDto::getSafetyScore));

        List<SummarizedOnSaleProductDto> summarizedProducts = products.getContent().stream()
                .map(product -> {
                    BigDecimal safetyScore = productSafetyScoreMap.getOrDefault(product.getId(), null);
                    return new SummarizedOnSaleProductDto(product, safetyScore);
                })
                .toList();

        return new PageImpl<>(summarizedProducts, pageable, products.getTotalElements());
    }

    public Page<SummarizedProductDto> listByEndSale(String type, Pageable pageable) {
        Sort sort = switch (type) {
            case "latest" -> Sort.by(Sort.Order.desc("subscriptionStartDate"), Sort.Order.desc("lastModifiedAt"));
            case "knock-in" -> Sort.by(Sort.Order.asc("knockIn").nullsLast(), Sort.Order.desc("lastModifiedAt"));
            case "profit" -> Sort.by(Sort.Order.desc("yieldIfConditionsMet"), Sort.Order.desc("lastModifiedAt"));
            case "deadline" -> Sort.by(Sort.Order.desc("subscriptionEndDate"), Sort.Order.desc("lastModifiedAt"));
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
        log.info("Before adding the products data");
        List<Product> productList = productRepository.listByIds(productIdList);
        log.info("After adding the products data");

        return productList.stream()
                .map(SummarizedProductDto::new)
                .collect(Collectors.toList());
    }

    public List<SummarizedProductForHoldingDto> holdingListByProductIds(List<Long> productIdList) {
        log.info("Before adding the products data");
        List<Product> productList = productRepository.listByIds(productIdList);
        log.info("After adding the products data");

        List<SummarizedProductForHoldingDto> summarizedProductForHoldingDtos = new ArrayList<>();
        for (Product product : productList) {
            summarizedProductForHoldingDtos.add(new SummarizedProductForHoldingDto(
                    product,
                    repaymentEvaluationDatesService.findNextRepaymentEvaluationDate(product.getId()).getNextRepaymentEvaluationDate()
            ));
        }
        return summarizedProductForHoldingDtos;
    }

    public ResponseSingleProductDto findOne(Long productId, Long userId) {
        log.info("Before retrieving the product data");
        Product product = productRepository.findOne(productId).orElseThrow(ProductNotFoundException::new);
        List<TickerSymbol> tickerSymbolList = tickerSymbolRepository.findTickerSymbolList(productId);

        Map<String, String> equityTickerSymbols = tickerSymbolList.stream()
                .collect(Collectors.toMap(TickerSymbol::getEquityName, TickerSymbol::getTickerSymbol));
        log.info("After adding the retrieved product data");

        boolean isLiked = likeService.isLiked(productId, userId);
        int likeCount = likeService.getCountOfLikes(productId);

        return new ResponseSingleProductDto(product, equityTickerSymbols, likeCount, isLiked);
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

    public List<SummarizedProductDto> searchProductByIssueNumber(Integer IssueNumber) {
        // 각 발행사에서 회차는 유니크하지만, 다른 발행사끼리 회차 번호가 겹칠 수 있기때문에 리스트로 반환
        List<Product> productList = productSearchRepository.searchByIssueNumber(IssueNumber);

        return productList.stream()
                .map(SummarizedProductDto::new)
                .collect(Collectors.toList());
    }

    public ResponseTodayReceivedProductIdsDto findTodayReceivedProductIds() {
        List<Product> productList = productRepository.listByCreatedAtToday();
        if (productList.isEmpty()) {
            throw new TodayReceivedProductsNotFoundException();
        }

        List<Long> productIdList = productList.stream()
                .map(Product::getId)
                .toList();
        return new ResponseTodayReceivedProductIdsDto(productIdList);
    }

    /**
     * 일일 인기 TOP5 상품 리스트 조회
     *
     * @return 좋아요 증감 + 조회수가 높은 상품 정보 리스트 반환
     */
    public List<SummarizedProductDto> getDailyTop5Products() {
        List<Long> productIdList = dailyHotProductService.getDailyTop5Products();
        List<Product> productList = productRepository.listByIds(productIdList);

        // List<Product>를 productIdList의 순서대로 다시 정렬
        Map<Long, Product> productMap = productList.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        List<Product> sortedProductList = productIdList.stream()
                .map(productMap::get)
                .toList();

        // 정렬된 Product 리스트를 SummarizedProductDto로 변환
        List<SummarizedProductDto> summarizedProductDtos = sortedProductList.stream()
                .map(SummarizedProductDto::new)
                .collect(Collectors.toList());

        return summarizedProductDtos;
    }

    private List<ResponseAIResultDto> listStepDownAIResult(List<Long> stepDownProductIds) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("aiResultCircuitBreaker");
        return circuitBreaker.run(() -> analysisServiceClient.getAIResultList(new RequestProductIdListDto(stepDownProductIds)),
                throwable -> new ArrayList<>());
    }
}
