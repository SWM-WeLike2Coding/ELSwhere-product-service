package com.wl2c.elswhereproductservice.domain.product.repository;

import com.wl2c.elswhereproductservice.domain.product.model.MaturityEvaluationDateType;
import com.wl2c.elswhereproductservice.domain.product.model.ProductState;
import com.wl2c.elswhereproductservice.domain.product.model.ProductType;
import com.wl2c.elswhereproductservice.domain.product.model.UnderlyingAssetType;
import com.wl2c.elswhereproductservice.domain.product.model.dto.list.SummarizedProductDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.request.RequestProductSearchDto;
import com.wl2c.elswhereproductservice.domain.product.model.entity.EarlyRepaymentEvaluationDates;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.domain.product.model.entity.ProductTickerSymbol;
import com.wl2c.elswhereproductservice.domain.product.model.entity.TickerSymbol;
import com.wl2c.elswhereproductservice.global.config.QueryDslConfig;
import com.wl2c.elswhereproductservice.mock.EarlyRepaymentEvaluationDatesMock;
import com.wl2c.elswhereproductservice.mock.ProductMock;
import com.wl2c.elswhereproductservice.mock.ProductTickerSymbolMock;
import com.wl2c.elswhereproductservice.mock.TickerSymbolMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import({ProductSearchRepository.class, QueryDslConfig.class})
public class ProductSearchRepositoryTest {

    @Autowired
    private EarlyRepaymentEvaluationDatesRepository earlyRepaymentEvaluationDatesRepository;

    @Autowired
    private ProductTickerSymbolRepository productTickerSymbolRepository;

    @Autowired
    private TickerSymbolRepository tickerSymbolRepository;

    @Autowired
    private ProductRepository productRepository;


    @Autowired
    private ProductSearchRepository productSearchRepository;

    Product product1, product2, product3;

    @BeforeEach
    void before() {
        earlyRepaymentEvaluationDatesRepository.deleteAll();
        productTickerSymbolRepository.deleteAll();
        tickerSymbolRepository.deleteAll();
        productRepository.deleteAll();

        List<TickerSymbol> tickerSymbols = Arrays.asList(
                TickerSymbolMock.create("005930.KS", "삼성전자", UnderlyingAssetType.STOCK),
                TickerSymbolMock.create("^GSPC", "S&P500", UnderlyingAssetType.INDEX),
                TickerSymbolMock.create("^KS200", "KOSPI200", UnderlyingAssetType.INDEX),
                TickerSymbolMock.create("TSLA", "Tesla", UnderlyingAssetType.STOCK),
                TickerSymbolMock.create("^HSCE", "HSCEI", UnderlyingAssetType.INDEX),
                TickerSymbolMock.create("NVDA", "NVIDIA", UnderlyingAssetType.STOCK)
        );

        product1 = ProductMock.create(
                "AA증권",
                "AA증권 1호",
                "삼성전자 / S&P500 / KOSPI200",
                3,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusYears(3),
                LocalDate.now().plusYears(3).minusDays(5),
                MaturityEvaluationDateType.SINGLE,
                new BigDecimal("10.423"),
                LocalDate.now().minusDays(14),
                LocalDate.now().minusDays(1),
                "95-90-85-80-75-50",
                45,
                ProductType.STEP_DOWN,
                ProductState.ACTIVE);

        product2 = ProductMock.create(
                "BB증권",
                "BB증권 2호",
                "S&P500 / HSCEI / KOSPI200",
                3,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusYears(3),
                LocalDate.now().plusYears(3).minusDays(3),
                MaturityEvaluationDateType.SINGLE,
                new BigDecimal("15.34"),
                LocalDate.now().minusDays(14),
                LocalDate.now(),
                "95-90-80-75-70-65",
                40,
                ProductType.STEP_DOWN,
                ProductState.ACTIVE);

        product3 = ProductMock.create(
                "CC증권",
                "CC증권 3호",
                "Tesla / 삼성전자 / NVIDIA",
                3,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusYears(3),
                LocalDate.now().plusYears(3).minusDays(5),
                MaturityEvaluationDateType.MULTIPLE,
                new BigDecimal("11.234"),
                LocalDate.now().minusDays(14),
                LocalDate.now(),
                "95-90(85)-80-75-70-65",
                50,
                ProductType.LIZARD,
                ProductState.ACTIVE);

        tickerSymbolRepository.saveAll(tickerSymbols);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        List<ProductTickerSymbol> productTickerSymbol1 = ProductTickerSymbolMock.createList(product1, Arrays.asList(
                tickerSymbols.get(0),
                tickerSymbols.get(1),
                tickerSymbols.get(2)
        ));
        productTickerSymbolRepository.saveAll(productTickerSymbol1);

        List<ProductTickerSymbol> productTickerSymbol2 = ProductTickerSymbolMock.createList(product2, Arrays.asList(
                tickerSymbols.get(1),
                tickerSymbols.get(4),
                tickerSymbols.get(2)
        ));
        productTickerSymbolRepository.saveAll(productTickerSymbol2);

        List<ProductTickerSymbol> productTickerSymbol3 = ProductTickerSymbolMock.createList(product3, Arrays.asList(
                tickerSymbols.get(3),
                tickerSymbols.get(0),
                tickerSymbols.get(5)
        ));
        productTickerSymbolRepository.saveAll(productTickerSymbol3);
    }

    @Test
    @DisplayName("상품 검색 - 원하는 이름을 포함하는 상품을 잘 가져오는지 확인1")
    void searchProductName_1() {
        // given
        RequestProductSearchDto searchDto = RequestProductSearchDto.builder()
                .productName("AA")
                .build();

        // when
        Page<SummarizedProductDto> result = productSearchRepository.search(searchDto, PageRequest.of(0, Integer.MAX_VALUE));

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("상품 검색 - 원하는 이름을 포함하는 상품을 잘 가져오는지 확인2")
    void searchProductName_2() {
        // given
        RequestProductSearchDto searchDto = RequestProductSearchDto.builder()
                .productName("증권")
                .build();

        // when
        Page<SummarizedProductDto> result = productSearchRepository.search(searchDto, PageRequest.of(0, Integer.MAX_VALUE));

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("상품 검색 - 원하는 조건의 발행회사 상품을 잘 가져오는지 확인")
    void searchPublisher() {
        // given
        RequestProductSearchDto searchDto = RequestProductSearchDto.builder()
                .issuer("AA증권")
                .build();

        // when
        Page<SummarizedProductDto> result = productSearchRepository.search(searchDto, PageRequest.of(0, Integer.MAX_VALUE));

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("상품 검색 - 원하는 조건의 기초자산 수를 가진 상품을 잘 가져오는지 확인")
    void searchEquityCount() {
        // given
        RequestProductSearchDto searchDto = RequestProductSearchDto.builder()
                .equityCount(3)
                .build();

        // when
        Page<SummarizedProductDto> result = productSearchRepository.search(searchDto, PageRequest.of(0, Integer.MAX_VALUE));

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("상품 검색 - 설정한 최대 낙인 이하의 상품을 잘 가져오는지 확인")
    void searchMaxKnockIn() {
        // given
        RequestProductSearchDto searchDto = RequestProductSearchDto.builder()
                .maxKnockIn(50)
                .build();

        // when
        Page<SummarizedProductDto> result = productSearchRepository.search(searchDto, PageRequest.of(0, Integer.MAX_VALUE));

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("상품 검색 - 최소 수익률 이상인 상품을 잘 가져오는지 확인")
    void searchMin() {
        // given
        RequestProductSearchDto searchDto = RequestProductSearchDto.builder()
                .minYieldIfConditionsMet(BigDecimal.valueOf(12))
                .build();

        // when
        Page<SummarizedProductDto> result = productSearchRepository.search(searchDto, PageRequest.of(0, Integer.MAX_VALUE));

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().stream().anyMatch(product -> product.getName().equals("BB증권 2호"))).isTrue();
    }

    @Test
    @DisplayName("상품 검색 - 설정한 1차 상환 배리어에 해당하는 상품을 잘 가져오는지 확인")
    void searchInitialRedemptionBarrier() {
        // given
        RequestProductSearchDto searchDto = RequestProductSearchDto.builder()
                .initialRedemptionBarrier(95)
                .build();

        // when
        Page<SummarizedProductDto> result = productSearchRepository.search(searchDto, PageRequest.of(0, Integer.MAX_VALUE));

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("상품 검색 - 설정한 상품 종류에 해당하는 상품을 잘 가져오는지 확인")
    void searchMaturityRedemptionBarrier() {
        // given
        RequestProductSearchDto searchDto = RequestProductSearchDto.builder()
                .type(ProductType.STEP_DOWN)
                .build();

        // when
        Page<SummarizedProductDto> result = productSearchRepository.search(searchDto, PageRequest.of(0, Integer.MAX_VALUE));

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("상품 검색 - 설정한 청약 기간에 속하는 상품을 잘 가져오는지 확인")
    void searchSubscriptionPeriod() {
        // given
        RequestProductSearchDto searchDto = RequestProductSearchDto.builder()
                .subscriptionStartDate(LocalDate.now().minusDays(20))
                .subscriptionEndDate(LocalDate.now())
                .build();

        // when
        Page<SummarizedProductDto> result = productSearchRepository.search(searchDto, PageRequest.of(0, Integer.MAX_VALUE));

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("상품 검색 - 특정 기초자산을 포함하고 있는 상품을 잘 가져오는지 확인")
    void searchEquityNamesIn() {
        // given
        List<String> equityNamesList = Arrays.asList("삼성전자", "S&P500");

        RequestProductSearchDto searchDto = RequestProductSearchDto.builder()
                .equityNames(equityNamesList)
                .build();

        // when
        Page<SummarizedProductDto> result = productSearchRepository.search(searchDto, PageRequest.of(0, Integer.MAX_VALUE));

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getEquities()).contains("삼성전자", "S&P500");
    }

    @Test
    @DisplayName("상품 검색 - 종목 형으로만 이루어진 상품을 잘 가져오는지 확인")
    void searchEquityTypeEqSTOCK() {
        // given
        RequestProductSearchDto searchDto = RequestProductSearchDto.builder()
                .equityType(UnderlyingAssetType.STOCK)
                .build();

        // when
        Page<SummarizedProductDto> result = productSearchRepository.search(searchDto, PageRequest.of(0, Integer.MAX_VALUE));

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getEquities()).contains("Tesla", "삼성전자", "NVIDIA");
    }

    @Test
    @DisplayName("상품 검색 - 주가 지수 형으로만 이루어진 상품을 잘 가져오는지 확인")
    void searchEquityTypeEqINDEX() {
        // given
        RequestProductSearchDto searchDto = RequestProductSearchDto.builder()
                .equityType(UnderlyingAssetType.INDEX)
                .build();

        // when
        Page<SummarizedProductDto> result = productSearchRepository.search(searchDto, PageRequest.of(0, Integer.MAX_VALUE));

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getEquities()).contains("S&P500", "HSCEI", "KOSPI200");
    }

    @Test
    @DisplayName("상품 검색 - 주가 지수과 종목형이 섞여있는 상품을 잘 가져오는지 확인")
    void searchEquityTypeEqMIX() {
        // given
        RequestProductSearchDto searchDto = RequestProductSearchDto.builder()
                .equityType(UnderlyingAssetType.MIX)
                .build();

        // when
        Page<SummarizedProductDto> result = productSearchRepository.search(searchDto, PageRequest.of(0, Integer.MAX_VALUE));

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getEquities()).contains("삼성전자", "S&P500", "KOSPI200");
    }

    @Test
    @DisplayName("상품 검색 - 설정한 조기상환일 간격에 맞는 상품을 잘 가져오는지 확인")
    void searchRedemptionIntervalEq() {
        // given
        List<EarlyRepaymentEvaluationDates> earlyRepaymentEvaluationDatesList = EarlyRepaymentEvaluationDatesMock.createList(product1, Arrays.asList(
                LocalDate.now().plusMonths(6),
                LocalDate.now().plusMonths(12),
                LocalDate.now().plusMonths(18),
                LocalDate.now().plusMonths(24),
                LocalDate.now().plusMonths(30)
        ));
        earlyRepaymentEvaluationDatesRepository.saveAll(earlyRepaymentEvaluationDatesList);

        RequestProductSearchDto searchDto = RequestProductSearchDto.builder()
                .redemptionInterval(6)
                .build();

        // when
        Page<SummarizedProductDto> result = productSearchRepository.search(searchDto, PageRequest.of(0, Integer.MAX_VALUE));

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getEquities()).contains("삼성전자", "S&P500", "KOSPI200");
    }
}