package com.wl2c.elswhereproductservice.domain.product.repository;

import com.wl2c.elswhereproductservice.domain.product.model.ProductState;
import com.wl2c.elswhereproductservice.domain.product.model.ProductType;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.mock.ProductMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

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
}
