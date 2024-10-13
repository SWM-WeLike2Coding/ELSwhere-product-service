package com.wl2c.elswhereproductservice.domain.product.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wl2c.elswhereproductservice.domain.like.model.LikeState;
import com.wl2c.elswhereproductservice.domain.product.model.dto.ProductLikeMessage;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@SpringJUnitConfig
@EmbeddedKafka(topics = {"product-like"})
class ProductLikeMessageSenderTest {

    @Autowired
    private ProductLikeMessageSender productLikeMessageSender;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("Kafka에 상품 좋아요 메시지를 전송하고, 메시지가 정상적으로 소비되는지 검증")
    public void testSendProductLikeMessage(@Autowired EmbeddedKafkaBroker embeddedKafkaBroker) throws Exception {
        // given
        ProductLikeMessage message = ProductLikeMessage.builder()
                .userId(1L)
                .productId(10L)
                .likeState(LikeState.LIKED)
                .build();

        // Kafka Consumer 설정
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
        Consumer<String, String> consumer = consumerFactory.createConsumer();

        // when
        productLikeMessageSender.send("product-like", message);

        // then
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "product-like");
        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, "product-like");
        ProductLikeMessage consumedMessage = objectMapper.readValue(singleRecord.value(), ProductLikeMessage.class);
        consumer.close();

        assertThat(consumedMessage.getUserId()).isEqualTo(1L);
        assertThat(consumedMessage.getProductId()).isEqualTo(10L);

    }
}