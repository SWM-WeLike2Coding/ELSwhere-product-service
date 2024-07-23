package com.wl2c.elswhereproductservice.domain.product.repository;

import com.wl2c.elswhereproductservice.domain.product.model.ProductState;
import com.wl2c.elswhereproductservice.domain.product.model.ProductType;
import com.wl2c.elswhereproductservice.domain.product.model.dto.list.SummarizedProductDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.request.RequestProductSearchDto;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.global.config.QueryDslConfig;
import com.wl2c.elswhereproductservice.mock.ProductMock;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import({ProductSearchRepository.class, QueryDslConfig.class})
public class ProductSearchRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSearchRepository productSearchRepository;

    @BeforeEach
    void before() {
        productRepository.deleteAll();

        Product product1 = ProductMock.create(
                "AA증권",
                "1호",
                "삼성전자 / S&P500 / KOSPI200",
                3,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusYears(3),
                new BigDecimal("10.423"),
                LocalDate.now().minusDays(14),
                LocalDate.now().minusDays(1),
                "95-90-85-80-75-50",
                45,
                ProductType.STEP_DOWN,
                ProductState.ACTIVE);

        Product product2 = ProductMock.create(
                "BB증권",
                "2호",
                "Tesla / HSCEI / NVIDIA",
                3,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusYears(3),
                new BigDecimal("15.34"),
                LocalDate.now().minusDays(14),
                LocalDate.now(),
                "95-90-80-75-70-65",
                40,
                ProductType.STEP_DOWN,
                ProductState.ACTIVE);

        productRepository.save(product1);
        productRepository.save(product2);
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
        assertThat(result.getTotalElements()).isEqualTo(2);
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
        assertThat(result.getTotalElements()).isEqualTo(2);
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
        assertThat(result.getContent().stream().anyMatch(product -> product.getName().equals("2호"))).isTrue();
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
        assertThat(result.getTotalElements()).isEqualTo(2);
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
        assertThat(result.getTotalElements()).isEqualTo(2);
    }
}