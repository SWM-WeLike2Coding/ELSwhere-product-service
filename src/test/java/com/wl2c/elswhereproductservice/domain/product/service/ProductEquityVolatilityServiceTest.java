package com.wl2c.elswhereproductservice.domain.product.service;

import com.wl2c.elswhereproductservice.domain.product.model.MaturityEvaluationDateType;
import com.wl2c.elswhereproductservice.domain.product.model.ProductState;
import com.wl2c.elswhereproductservice.domain.product.model.ProductType;
import com.wl2c.elswhereproductservice.domain.product.model.UnderlyingAssetType;
import com.wl2c.elswhereproductservice.domain.product.model.dto.response.ResponseProductEquityVolatilityDto;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.domain.product.model.entity.ProductEquityVolatility;
import com.wl2c.elswhereproductservice.domain.product.model.entity.ProductTickerSymbol;
import com.wl2c.elswhereproductservice.domain.product.model.entity.TickerSymbol;
import com.wl2c.elswhereproductservice.domain.product.repository.ProductEquityVolatilityRepository;
import com.wl2c.elswhereproductservice.domain.product.repository.ProductRepository;
import com.wl2c.elswhereproductservice.domain.product.repository.ProductTickerSymbolRepository;
import com.wl2c.elswhereproductservice.mock.ProductEquityVolatilityMock;
import com.wl2c.elswhereproductservice.mock.ProductMock;
import com.wl2c.elswhereproductservice.mock.ProductTickerSymbolMock;
import com.wl2c.elswhereproductservice.mock.TickerSymbolMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductEquityVolatilityServiceTest {

    @Mock
    private ProductEquityVolatilityRepository productEquityVolatilityRepository;

    @Mock
    private ProductTickerSymbolRepository productTickerSymbolRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductEquityVolatilityService productEquityVolatilityService;

    @Test
    @DisplayName("상품에 해당하는 각 기초자산들의 변동성 값을 잘 가져오는지 확인")
    public void findProductEquityVolatilities() {
        // given
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

        List<ProductTickerSymbol> productTickerSymbolList = ProductTickerSymbolMock.createList(product,
                Arrays.asList(tickerSymbols.get(0), tickerSymbols.get(1), tickerSymbols.get(2)),
                Arrays.asList(1L, 2L, 3L)
        );
        List<BigDecimal> volatilityList = Arrays.asList(
                BigDecimal.valueOf(13.3333),
                BigDecimal.valueOf(53.244505),
                BigDecimal.valueOf(70.98242)
        );
        List<ProductEquityVolatility> productEquityVolatilityList = ProductEquityVolatilityMock.createList(productTickerSymbolList, volatilityList, Arrays.asList(1L, 2L, 3L));

        when(productRepository.findOne((Long) any())).thenReturn(Optional.ofNullable(product));
        when(productTickerSymbolRepository.findAllByProductId(any())).thenReturn(productTickerSymbolList);
        when(productEquityVolatilityRepository.findAllByProductTickerSymbol(any())).thenReturn(productEquityVolatilityList);

        // when
        ResponseProductEquityVolatilityDto responseProductEquityVolatilityDto = productEquityVolatilityService.findProductEquityVolatilities(1L);

        // then
        assertThat(responseProductEquityVolatilityDto.getEquityList().size()).isEqualTo(3);
        assertThat(responseProductEquityVolatilityDto.getEquityVolatilities().get("삼성전자")).isEqualTo(new BigDecimal("13.3333"));
        assertThat(responseProductEquityVolatilityDto.getEquityVolatilities().get("S&P500")).isEqualTo(new BigDecimal("53.244505"));
        assertThat(responseProductEquityVolatilityDto.getEquityVolatilities().get("KOSPI200")).isEqualTo(new BigDecimal("70.98242"));
    }

}