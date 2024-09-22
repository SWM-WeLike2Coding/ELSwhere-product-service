package com.wl2c.elswhereproductservice.domain.product.service;

import com.wl2c.elswhereproductservice.domain.product.exception.ProductEarlyRepaymentEvaluationDateFoundException;
import com.wl2c.elswhereproductservice.domain.product.exception.ProductMaturityEvaluationDateNotFoundException;
import com.wl2c.elswhereproductservice.domain.product.exception.ProductNotFoundException;
import com.wl2c.elswhereproductservice.domain.product.model.dto.response.ResponseMaturityRepaymentEvaluationDateDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.response.ResponseNextRepaymentEvaluationDateDto;
import com.wl2c.elswhereproductservice.domain.product.model.entity.EarlyRepaymentEvaluationDates;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.domain.product.repository.EarlyRepaymentEvaluationDatesRepository;
import com.wl2c.elswhereproductservice.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepaymentEvaluationDatesService {

    private final EarlyRepaymentEvaluationDatesRepository earlyRepaymentEvaluationDatesRepository;
    private final ProductRepository productRepository;

    public ResponseNextRepaymentEvaluationDateDto findNextRepaymentEvaluationDate(Long productId) {

        List<EarlyRepaymentEvaluationDates> earlyRepaymentEvaluationDatesList = earlyRepaymentEvaluationDatesRepository.findAllEarlyRepaymentEvaluationDate(productId);

        int order;
        LocalDate nextDate;
        if (earlyRepaymentEvaluationDatesRepository.findNextEarlyRepaymentEvaluationDate(productId).isEmpty()) {
            // 자동 조기 상환일이 다 끝났다면, 마지막 만기 상환 평가일을 보여주도록함
            nextDate = productRepository.findOne(productId).orElseThrow(ProductNotFoundException::new).getMaturityEvaluationDate();
            if (nextDate == null) {
                throw new ProductMaturityEvaluationDateNotFoundException(productId);
            }
            order = earlyRepaymentEvaluationDatesList.size() + 1;

        } else {
            nextDate = earlyRepaymentEvaluationDatesRepository.findNextEarlyRepaymentEvaluationDate(productId).get();
            order = earlyRepaymentEvaluationDatesRepository.findNextEarlyRepaymentEvaluationDateOrder(productId, nextDate).get();
        }
        return new ResponseNextRepaymentEvaluationDateDto(order, nextDate);
    }

    public ResponseMaturityRepaymentEvaluationDateDto findMaturityRepaymentEvaluationDate(Long productId) {

        Product product = productRepository.findOne(productId).orElseThrow(ProductNotFoundException::new);
        if (product.getMaturityEvaluationDate() == null) {
            throw new ProductMaturityEvaluationDateNotFoundException();
        }

        return new ResponseMaturityRepaymentEvaluationDateDto(
                product.getMaturityEvaluationDate(),
                product.getMaturityEvaluationDateType()
        );
    }
}
