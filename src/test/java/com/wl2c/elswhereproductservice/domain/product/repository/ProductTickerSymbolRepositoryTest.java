package com.wl2c.elswhereproductservice.domain.product.repository;

import com.wl2c.elswhereproductservice.domain.product.model.MaturityEvaluationDateType;
import com.wl2c.elswhereproductservice.domain.product.model.ProductState;
import com.wl2c.elswhereproductservice.domain.product.model.ProductType;
import com.wl2c.elswhereproductservice.domain.product.model.UnderlyingAssetType;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.domain.product.model.entity.ProductTickerSymbol;
import com.wl2c.elswhereproductservice.domain.product.model.entity.TickerSymbol;
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
public class ProductTickerSymbolRepositoryTest {

    @Autowired
    private TickerSymbolRepository tickerSymbolRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductTickerSymbolRepository productTickerSymbolRepository;

    Product product;

    @BeforeEach
    void before() {
        tickerSymbolRepository.deleteAll();
        productRepository.deleteAll();
        productTickerSymbolRepository.deleteAll();

        List<TickerSymbol> tickerSymbols = Arrays.asList(
                TickerSymbolMock.create("005930.KS", "삼성전자", UnderlyingAssetType.STOCK),
                TickerSymbolMock.create("^GSPC", "S&P500", UnderlyingAssetType.INDEX),
                TickerSymbolMock.create("^KS200", "KOSPI200", UnderlyingAssetType.INDEX)
        );

        product = ProductMock.create(
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
                UnderlyingAssetType.MIX,
                ProductState.ACTIVE);

        tickerSymbolRepository.saveAll(tickerSymbols);
        productRepository.save(product);

        List<ProductTickerSymbol> productTickerSymbolList = ProductTickerSymbolMock.createList(product, Arrays.asList(
                tickerSymbols.get(0),
                tickerSymbols.get(1),
                tickerSymbols.get(2)
        ));
        productTickerSymbolRepository.saveAll(productTickerSymbolList);
    }

    @Test
    @DisplayName("상품 id에 해당하는 티커 심볼들을 잘 가져오는지 확인")
    void findAllByProductId() {
        // when
        List<ProductTickerSymbol> productTickerSymbolList = productTickerSymbolRepository.findAllByProductId(product.getId());

        // then
        for (ProductTickerSymbol productTickerSymbol : productTickerSymbolList) {
            assertThat(productTickerSymbol.getProduct().getId()).isEqualTo(product.getId());
        }
        assertThat(productTickerSymbolList.size()).isEqualTo(3);
        assertThat(productTickerSymbolList.get(0).getTickerSymbol().getTickerSymbol()).containsAnyOf("005930.KS", "^GSPC", "^KS200");
        assertThat(productTickerSymbolList.get(1).getTickerSymbol().getTickerSymbol()).containsAnyOf("005930.KS", "^GSPC", "^KS200");
        assertThat(productTickerSymbolList.get(2).getTickerSymbol().getTickerSymbol()).containsAnyOf("005930.KS", "^GSPC", "^KS200");
    }
}
