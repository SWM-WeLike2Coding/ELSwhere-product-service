package com.wl2c.elswhereproductservice.domain.product.repository;

import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wl2c.elswhereproductservice.domain.product.model.ProductState;
import com.wl2c.elswhereproductservice.domain.product.model.ProductType;
import com.wl2c.elswhereproductservice.domain.product.model.dto.list.QSummarizedProductDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.list.SummarizedProductDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.request.RequestProductSearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.util.StringUtils.hasText;
import static com.wl2c.elswhereproductservice.domain.product.model.entity.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductSearchRepository {

    private final JPAQueryFactory queryFactory;

    public Page<SummarizedProductDto> search(RequestProductSearchDto requestDto,
                                                 Pageable pageable) {
        List<SummarizedProductDto> content = queryFactory
                .select(new QSummarizedProductDto(product))
                .from(product)
                .where(
                        equityCountEq(requestDto.getEquityCount()),
                        publisherEq(requestDto.getPublisher()),
                        knockInLoe(requestDto.getMaxKnockIn()),
                        yieldIfConditionsMetGoe(requestDto.getMinYieldIfConditionsMet()),
                        initialRedemptionBarrierEq(requestDto.getInitialRedemptionBarrier()),
                        maturityRedemptionBarrierEq(requestDto.getMaturityRedemptionBarrier()),
                        subscriptionPeriodEq(requestDto.getSubscriptionPeriod()),
                        typeEq(requestDto.getType()),
                        periodBetween(requestDto.getSubscriptionStartDate(), requestDto.getSubscriptionEndDate()),
                        product.productState.eq(ProductState.ACTIVE)
                )
                .orderBy(product.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(
                        equityCountEq(requestDto.getEquityCount()),
                        publisherEq(requestDto.getPublisher()),
                        knockInLoe(requestDto.getMaxKnockIn()),
                        yieldIfConditionsMetGoe(requestDto.getMinYieldIfConditionsMet()),
                        initialRedemptionBarrierEq(requestDto.getInitialRedemptionBarrier()),
                        maturityRedemptionBarrierEq(requestDto.getMaturityRedemptionBarrier()),
                        subscriptionPeriodEq(requestDto.getSubscriptionPeriod()),
                        typeEq(requestDto.getType()),
                        periodBetween(requestDto.getSubscriptionStartDate(), requestDto.getSubscriptionEndDate()),
                        product.productState.eq(ProductState.ACTIVE)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // TODO: 기초자산 명

    // 기초자산 개수
    private BooleanExpression equityCountEq(Integer equityCount) {
        return equityCount != null ? product.equityCount.eq(equityCount) : null;
    }

    // 발행회사
    private BooleanExpression publisherEq(String publisher) {
        return hasText(publisher) ? product.publisher.eq(publisher) : null;
    }

    // 최대 KI
    private BooleanExpression knockInLoe(Integer maxKnockIn) {
        return maxKnockIn != null ? product.knockIn.loe(maxKnockIn) : null;
    }

    // 최소 수익률
    private BooleanExpression yieldIfConditionsMetGoe(BigDecimal minYieldIfConditionsMet) {
        return minYieldIfConditionsMet != null ? product.yieldIfConditionsMet.goe(minYieldIfConditionsMet) : null;
    }

    // 1차 상환 배리어
    private BooleanExpression initialRedemptionBarrierEq(Integer initialRedemptionBarrier) {
        if (initialRedemptionBarrier != null) {
            // productInfo 문자열의 맨 앞 숫자를 추출
            StringTemplate firstNumberString = Expressions.stringTemplate(
                    "REGEXP_SUBSTR({0}, '^[0-9]+')",
                    product.productInfo
            );

            NumberTemplate<Integer> firstNumber = Expressions.numberTemplate(
                    Integer.class,
                    "CAST({0} AS DOUBLE)",
                    firstNumberString
            );

            return firstNumber.eq(initialRedemptionBarrier);
        }
        return null;
    }

    // 만기 상환 배리어
    private BooleanExpression maturityRedemptionBarrierEq(Integer maturityRedemptionBarrier) {
        if (maturityRedemptionBarrier != null) {
            // productInfo 문자열의 맨 뒤 숫자를 추출
            StringTemplate firstNumberString = Expressions.stringTemplate(
                    "SUBSTRING_INDEX(SUBSTRING_INDEX({0}, '-', -1), '(', 1)",
                    product.productInfo
            );

            NumberTemplate<Integer> firstNumber = Expressions.numberTemplate(
                    Integer.class,
                    "CAST({0} AS DOUBLE)",
                    firstNumberString
            );

            return firstNumber.eq(maturityRedemptionBarrier);
        }
        return null;
    }

    // 상품 가입 기간
    private BooleanExpression subscriptionPeriodEq(Integer subscriptionPeriod) {
        if (subscriptionPeriod != null) {
            // 두 날짜 사이의 월 수 계산
            NumberTemplate<Integer> monthsDiff = Expressions.numberTemplate(
                    Integer.class,
                    "PERIOD_DIFF(DATE_FORMAT({1}, '%Y%m'), DATE_FORMAT({0}, '%Y%m'))",
                    product.issuedDate,
                    product.maturityDate
            );

            // MONTHS_DIFF 값을 실수로 변환하고 12로 나누기
            NumberTemplate<Double> monthsAsDouble = Expressions.numberTemplate(
                    Double.class,
                    "CAST({0} AS DOUBLE)",
                    monthsDiff
            );

            NumberTemplate<Double> yearsDiff = Expressions.numberTemplate(
                    Double.class,
                    "{0} / 12",
                    monthsAsDouble
            );

            // 반올림
            NumberTemplate<Double> roundedYears = Expressions.numberTemplate(
                    Double.class,
                    "CEIL({0})",
                    yearsDiff
            );

            return roundedYears.eq(subscriptionPeriod.doubleValue());
        }
        return null;
    }

    // TODO: 상환일 간격

    // TODO: 기초자산 유형

    // 상품 유형
    private BooleanExpression typeEq(ProductType type) {
        return type != null ? product.type.eq(type) : null;
    }

    // 청약 시작일 & 청약 마감일
    private BooleanExpression periodBetween(LocalDate subscriptionStartDate, LocalDate subscriptionEndDate) {
        if (subscriptionStartDate != null && subscriptionEndDate != null) {
            return product.subscriptionStartDate.goe(subscriptionStartDate).and(product.subscriptionEndDate.loe(subscriptionEndDate));
        } else if (subscriptionStartDate != null) {
            return product.subscriptionStartDate.goe(subscriptionStartDate);
        } else if (subscriptionEndDate != null) {
            return product.subscriptionEndDate.loe(subscriptionEndDate);
        }
        return null;
    }
}
