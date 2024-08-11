package com.wl2c.elswhereproductservice.domain.product.repository;

import com.wl2c.elswhereproductservice.domain.product.model.entity.EarlyRepaymentEvaluationDates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EarlyRepaymentEvaluationDatesRepository extends JpaRepository<EarlyRepaymentEvaluationDates, Long> {

    @Query("select count(e) + 1 from EarlyRepaymentEvaluationDates e " +
            "where e.product.id = :productId " +
            "and e.earlyRepaymentEvaluationDate < :targetDate ")
    Optional<Integer> findNextEarlyRepaymentEvaluationDateOrder(@RequestParam("productId") Long productId,
                                                                @RequestParam("targetDate") LocalDate targetDate);

    @Query("select e.earlyRepaymentEvaluationDate from EarlyRepaymentEvaluationDates e " +
            "where e.product.productState = 'ACTIVE' " +
            "and e.product.id = :productId " +
            "and e.earlyRepaymentEvaluationDate > CURRENT_DATE " +
            "order by e.earlyRepaymentEvaluationDate asc " +
            "LIMIT 1 ")
    Optional<LocalDate> findNextEarlyRepaymentEvaluationDate(@RequestParam("productId") Long productId);

    @Query("select e from EarlyRepaymentEvaluationDates e " +
            "where e.product.productState = 'ACTIVE' " +
            "and e.product.id = :productId ")
    List<EarlyRepaymentEvaluationDates> findAllEarlyRepaymentEvaluationDate(@RequestParam("productId") Long productId);
}
