package com.wl2c.elswhereproductservice.domain.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wl2c.elswhereproductservice.domain.product.model.dto.ProductLikeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductLikeMessageSender {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, ProductLikeMessage productLikeMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            String stringMessage = objectMapper.writeValueAsString(productLikeMessage);
            log.info("product-like Message Created : " + stringMessage);

            kafkaTemplate.send(topic, stringMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
