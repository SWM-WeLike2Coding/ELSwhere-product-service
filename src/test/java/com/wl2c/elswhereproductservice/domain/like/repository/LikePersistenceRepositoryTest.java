package com.wl2c.elswhereproductservice.domain.like.repository;

import com.wl2c.elswhereproductservice.domain.like.model.entity.LikeElement;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.domain.product.repository.ProductRepository;
import com.wl2c.elswhereproductservice.mock.LikeElementMock;
import com.wl2c.elswhereproductservice.mock.ProductMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class LikePersistenceRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LikePersistenceRepository likePersistenceRepository;

    private Product product;

    @BeforeEach
    void before() {
        likePersistenceRepository.deleteAll();

        product = ProductMock.create();
        productRepository.save(product);
    }

    @Test
    @DisplayName("상품 id에 대한 좋아요 개수를 잘 가져오는지 확인")
    void findLikeCountByProductId() {
        // given
        LikeElement likeElement = LikeElementMock.create(product, 3);
        likePersistenceRepository.save(likeElement);

        // when
        Integer count = likePersistenceRepository.findLikeCountByProductId(product.getId());

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("상품 id에 대한 좋아요 개수 변경이 잘 되는지 확인 - 더하기")
    void updateProductLikeCountPlus() {
        // given
        LikeElement likeElement = LikeElementMock.create(product, 3);
        likePersistenceRepository.save(likeElement);

        // when
        likePersistenceRepository.updateProductLikeCount(product.getId(), 2);
        Integer count = likePersistenceRepository.findLikeCountByProductId(product.getId());

        // then
        assertThat(count).isEqualTo(5);
    }

    @Test
    @DisplayName("상품 id에 대한 좋아요 개수 변경이 잘 되는지 확인 - 빼기")
    void updateProductLikeCountMinus() {
        // given
        LikeElement likeElement = LikeElementMock.create(product, 3);
        likePersistenceRepository.save(likeElement);

        // when
        likePersistenceRepository.updateProductLikeCount(product.getId(), -2);
        Integer count = likePersistenceRepository.findLikeCountByProductId(product.getId());

        // then
        assertThat(count).isEqualTo(1);
    }
}