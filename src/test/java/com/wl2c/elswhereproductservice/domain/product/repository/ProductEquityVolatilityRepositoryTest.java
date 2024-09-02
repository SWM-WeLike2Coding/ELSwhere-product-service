package com.wl2c.elswhereproductservice.domain.product.repository;

import com.wl2c.elswhereproductservice.domain.product.model.MaturityEvaluationDateType;
import com.wl2c.elswhereproductservice.domain.product.model.ProductState;
import com.wl2c.elswhereproductservice.domain.product.model.ProductType;
import com.wl2c.elswhereproductservice.domain.product.model.UnderlyingAssetType;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.domain.product.model.entity.ProductEquityVolatility;
import com.wl2c.elswhereproductservice.domain.product.model.entity.ProductTickerSymbol;
import com.wl2c.elswhereproductservice.domain.product.model.entity.TickerSymbol;
import com.wl2c.elswhereproductservice.mock.ProductEquityVolatilityMock;
import com.wl2c.elswhereproductservice.mock.ProductMock;
import com.wl2c.elswhereproductservice.mock.ProductTickerSymbolMock;
import com.wl2c.elswhereproductservice.mock.TickerSymbolMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class ProductEquityVolatilityRepositoryTest {

    @Autowired
    private TickerSymbolRepository tickerSymbolRepository;

    @Autowired
    private ProductTickerSymbolRepository productTickerSymbolRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductEquityVolatilityRepository productEquityVolatilityRepository;

    List<ProductTickerSymbol> productTickerSymbolList;

    @BeforeEach
    void before() {
        tickerSymbolRepository.deleteAll();
        productTickerSymbolRepository.deleteAll();

        List<TickerSymbol> tickerSymbols = Arrays.asList(
                TickerSymbolMock.create("005930.KS", "삼성전자", UnderlyingAssetType.STOCK),
                TickerSymbolMock.create("^GSPC", "S&P500", UnderlyingAssetType.INDEX),
                TickerSymbolMock.create("^KS200", "KOSPI200", UnderlyingAssetType.INDEX)
        );

        Product product = ProductMock.create(
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

        tickerSymbolRepository.saveAll(tickerSymbols);
        productRepository.save(product);

        productTickerSymbolList = ProductTickerSymbolMock.createList(product, Arrays.asList(
                tickerSymbols.get(0),
                tickerSymbols.get(1),
                tickerSymbols.get(2)
        ));
        productTickerSymbolRepository.saveAll(productTickerSymbolList);
    }

    @Test
    @DisplayName("각 기초자산들의 변동성 값을 잘 가져오는지 확인")
    void findAllByProductTickerSymbol() {
        // given
        List<Long> productTickerSymbolIdList = productTickerSymbolList.stream()
                .map(ProductTickerSymbol::getId)
                .toList();

        List<BigDecimal> volatilityList = Arrays.asList(
                BigDecimal.valueOf(13.3333),
                BigDecimal.valueOf(53.244505),
                BigDecimal.valueOf(70.98242)
        );

        List<ProductEquityVolatility> productEquityVolatilityList = ProductEquityVolatilityMock.createList(productTickerSymbolList, volatilityList);
        productEquityVolatilityRepository.saveAll(productEquityVolatilityList);

        // when
        List<ProductEquityVolatility> responseProductEquityVolatilityList = productEquityVolatilityRepository.findAllByProductTickerSymbol(productTickerSymbolIdList);

        // then
        assertThat(responseProductEquityVolatilityList.size()).isEqualTo(3);
        for (ProductEquityVolatility productEquityVolatility : responseProductEquityVolatilityList) {
            for (int i = 0; i < productTickerSymbolList.size(); i++) {
                if (productEquityVolatility.getProductTickerSymbol().equals(productTickerSymbolList.get(i)))
                    assertThat(productEquityVolatility.getVolatility()).isEqualTo(volatilityList.get(i));
            }
        }
    }
}
