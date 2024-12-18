package com.wl2c.elswhereproductservice.domain.product.repository;

import com.wl2c.elswhereproductservice.domain.product.model.MaturityEvaluationDateType;
import com.wl2c.elswhereproductservice.domain.product.model.ProductState;
import com.wl2c.elswhereproductservice.domain.product.model.ProductType;
import com.wl2c.elswhereproductservice.domain.product.model.UnderlyingAssetType;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.mock.ProductMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    Product product1, product2;

    @BeforeEach
    void before() {
        productRepository.deleteAll();

        product1 = ProductMock.create(
                "AA증권",
                "1호",
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

        product2 = ProductMock.create(
                "BB증권",
                "2호",
                "Tesla / HSCEI / NVIDIA",
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
                UnderlyingAssetType.MIX,
                ProductState.ACTIVE);

        productRepository.save(product1);
        productRepository.save(product2);
    }

    @Test
    @DisplayName("청약 중인 상품인지를 잘 구분하는지 확인")
    void findOnSale() {
        // given & when
        Page<Product> result = productRepository.listByOnSale("knock-in", Pageable.unpaged());

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().stream().anyMatch(product -> product.getName().equals("2호"))).isTrue();
    }

    @Test
    @DisplayName("청약 종료인 상품인지를 잘 구분하는지 확인")
    void findEndSale() {
        // given & when
        Page<Product> result = productRepository.listByEndSale("knock-in", Pageable.unpaged());

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().stream().anyMatch(product -> product.getName().equals("1호"))).isTrue();
    }

    @Test
    @DisplayName("상품 id 리스트에 해당하는 상품 리스트를 잘 가져오는지 확인")
    void listByIds() {
        // given & when
        List<Long> list = Arrays.asList(product1.getId(), product2.getId());
        List<Product> productList = productRepository.listByIds(list);

        // then
        assertThat(productList.size()).isEqualTo(2);
        assertThat(productList.get(0).getIssuer()).isEqualTo("AA증권");
        assertThat(productList.get(1).getIssuer()).isEqualTo("BB증권");
    }

}
