package com.wl2c.elswhereproductservice.domain.product.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ResponseNextRepaymentEvaluationDateDto {

    @Schema(description = "차수", example = "2")
    private final int order;

    @Schema(description = "다음 상환평가일", example = "2027-01-13")
    private final LocalDate nextRepaymentEvaluationDate;

    public ResponseNextRepaymentEvaluationDateDto(int order,
                                                  LocalDate nextRepaymentEvaluationDate) {
        this.order = order;
        this.nextRepaymentEvaluationDate = nextRepaymentEvaluationDate;
    }
}
