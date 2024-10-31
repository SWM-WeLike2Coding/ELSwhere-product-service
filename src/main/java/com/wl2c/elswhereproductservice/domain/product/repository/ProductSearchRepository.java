package com.wl2c.elswhereproductservice.domain.product.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wl2c.elswhereproductservice.domain.product.model.ProductState;
import com.wl2c.elswhereproductservice.domain.product.model.ProductType;
import com.wl2c.elswhereproductservice.domain.product.model.UnderlyingAssetType;
import com.wl2c.elswhereproductservice.domain.product.model.dto.request.RequestProductSearchDto;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import static org.springframework.util.StringUtils.hasText;
import static com.wl2c.elswhereproductservice.domain.product.model.entity.QProduct.product;
import static com.wl2c.elswhereproductservice.domain.product.model.entity.QEarlyRepaymentEvaluationDates.earlyRepaymentEvaluationDates;
import static com.wl2c.elswhereproductservice.domain.product.model.entity.QTickerSymbol.tickerSymbol1;
import static com.wl2c.elswhereproductservice.domain.product.model.entity.QProductTickerSymbol.productTickerSymbol;

@Repository
@RequiredArgsConstructor
public class ProductSearchRepository {

    private final JPAQueryFactory queryFactory;

    public List<Product> searchByIssueNumber(Integer issueNumber) {
        return queryFactory.selectFrom(product)
                .where(
                        issueNumberEq(issueNumber),
                        product.productState.eq(ProductState.ACTIVE)
                )
                .orderBy(product.id.desc())
                .fetch();
    }

    public List<Product> search(RequestProductSearchDto requestDto,
                                Pageable pageable) {
        return queryFactory
                .selectFrom(product)
                .where(
                        productNameContain(requestDto.getProductName()),
                        equityNamesIn(requestDto.getEquityNames()),
                        equityCountEq(requestDto.getEquityCount()),
                        issuerEq(requestDto.getIssuer()),
                        knockInLoe(requestDto.getMaxKnockIn()),
                        yieldIfConditionsMetGoe(requestDto.getMinYieldIfConditionsMet()),
                        initialRedemptionBarrierEq(requestDto.getInitialRedemptionBarrier()),
                        maturityRedemptionBarrierEq(requestDto.getMaturityRedemptionBarrier()),
                        subscriptionPeriodEq(requestDto.getSubscriptionPeriod()),
                        redemptionIntervalEq(requestDto.getRedemptionInterval()),
                        equityTypeEq(requestDto.getEquityType()),
                        typeEq(requestDto.getType()),
                        periodBetween(requestDto.getSubscriptionStartDate(), requestDto.getSubscriptionEndDate()),
                        product.productState.eq(ProductState.ACTIVE)
                )
                .orderBy(product.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    // 상품 명 (일부 가능)
    private BooleanExpression productNameContain(String name) {
        return name != null ? product.name.contains(name) : null;
    }

    // 기초자산 명
    private BooleanExpression equityNamesIn(List<String> equityNames) {
        if (equityNames != null) {
            List<String> tickerSymbolList = new ArrayList<>();

            for (String equityName : equityNames) {
                tickerSymbolList.add(queryFactory
                        .select(tickerSymbol1.tickerSymbol)
                        .from(tickerSymbol1)
                        .where(tickerSymbol1.equityName.eq(equityName))
                        .fetchOne());
            }

            List<Long> result = queryFactory
                    .select(productTickerSymbol.product.id)
                    .from(productTickerSymbol)
                    .where(productTickerSymbol.tickerSymbol.tickerSymbol.in(tickerSymbolList))
                    .groupBy(productTickerSymbol.product.id)
                    .having(productTickerSymbol.tickerSymbol.tickerSymbol.count().eq((long) tickerSymbolList.size()))
                    .fetch();

            return product.id.in(result);
        }
        return null;
    }

    // 기초자산 개수
    private BooleanExpression equityCountEq(Integer equityCount) {
        return equityCount != null ? product.equityCount.eq(equityCount) : null;
    }

    // 발행회사
    private BooleanExpression issuerEq(String issuer) {
        return hasText(issuer) ? product.issuer.eq(issuer) : null;
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

    // 상환일 간격
    private BooleanExpression redemptionIntervalEq(Integer redemptionInterval) {
        if (redemptionInterval != null) {
            List<Tuple> results = queryFactory
                    .select(earlyRepaymentEvaluationDates.product.id, earlyRepaymentEvaluationDates.earlyRepaymentEvaluationDate)
                    .from(earlyRepaymentEvaluationDates)
                    .groupBy(earlyRepaymentEvaluationDates.product.id, earlyRepaymentEvaluationDates.earlyRepaymentEvaluationDate)
                    .orderBy(earlyRepaymentEvaluationDates.product.id.asc(), earlyRepaymentEvaluationDates.earlyRepaymentEvaluationDate.asc())
                    .fetch();

            Map<Long, List<LocalDate>> productDatesMap = new HashMap<>();
            List<Long> matchingProductIds = new ArrayList<>();

            for (Tuple tuple : results) {
                Long productId = tuple.get(0, Long.class);
                LocalDate date = tuple.get(1, LocalDate.class);

                if (productDatesMap.containsKey(productId) && productDatesMap.get(productId).size() == 2) {
                    continue;
                }

                productDatesMap.computeIfAbsent(productId, k -> new ArrayList<>()).add(date);
            }

            for (Map.Entry<Long, List<LocalDate>> entry : productDatesMap.entrySet()) {
                Long productId = entry.getKey();
                List<LocalDate> dates = entry.getValue();

                if (dates.size() == 2) {
                    LocalDate firstDate = dates.get(0);
                    LocalDate secondDate = dates.get(1);

                    Period period = Period.between(firstDate, secondDate);
                    int monthsDifference = period.getYears() * 12 + period.getMonths();
                    if (period.getDays() >= 20) {
                        monthsDifference += 1;
                    }

                    if (monthsDifference == redemptionInterval) {
                        matchingProductIds.add(productId);
                    }
                }
            }
            return product.id.in(matchingProductIds);
        }
        return null;
    }

    // 기초자산 유형
    private BooleanExpression equityTypeEq(UnderlyingAssetType type) {
        List<Long> result = new ArrayList<>();

        if (type != null) {
            if (type == UnderlyingAssetType.INDEX || type == UnderlyingAssetType.STOCK) {
                List<Tuple> tmp = queryFactory
                        .select(productTickerSymbol.product.id, productTickerSymbol.product.id.count())
                        .from(productTickerSymbol)
                        .join(productTickerSymbol.tickerSymbol, tickerSymbol1)
                        .where(productTickerSymbol.tickerSymbol.underlyingAssetType.eq(type))
                        .groupBy(productTickerSymbol.product.id)
                        .fetch();

                BooleanBuilder predicate = new BooleanBuilder();
                for (Tuple tuple : tmp) {
                    Long productId = tuple.get(productTickerSymbol.product.id);
                    Long count = tuple.get(productTickerSymbol.product.id.count());

                    predicate.or(
                            product.id.eq(productId).and(product.equityCount.eq(count.intValue()))
                    );
                }

                result = queryFactory
                        .select(product.id)
                        .from(product)
                        .where(predicate)
                        .groupBy(product.id)
                        .fetch();
                return product.id.in(result);

            } else if (type.equals(UnderlyingAssetType.MIX)) {
                result = queryFactory
                        .select(productTickerSymbol.product.id)
                        .from(productTickerSymbol)
                        .join(productTickerSymbol.tickerSymbol, tickerSymbol1)
                        .where(productTickerSymbol.tickerSymbol.underlyingAssetType.in(UnderlyingAssetType.STOCK, UnderlyingAssetType.INDEX))
                        .groupBy(productTickerSymbol.product.id)
                        .having(
                                productTickerSymbol.tickerSymbol.underlyingAssetType.countDistinct().eq(2L)
                        )
                        .fetch();
            }
            return product.id.in(result);
        }
        return null;
    }

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

    // 회차 번호
    private BooleanExpression issueNumberEq(Integer issueNumber) {
        return issueNumber != null ? product.issueNumber.eq(issueNumber) : null;
    }
}
