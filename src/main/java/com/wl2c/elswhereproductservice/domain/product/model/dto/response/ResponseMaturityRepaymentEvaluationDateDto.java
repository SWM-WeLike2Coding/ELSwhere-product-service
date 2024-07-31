package com.wl2c.elswhereproductservice.domain.product.model.dto.response;

import com.wl2c.elswhereproductservice.domain.product.model.MaturityEvaluationDateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ResponseMaturityRepaymentEvaluationDateDto {

    @Schema(description = "만기상환평가일", example = "2027-07-13")
    private final LocalDate maturityRepaymentEvaluationDate;

    @Schema(description = "만기상환평가일 개수", example = "SINGLE or MULTIPLE or UNKNOWN")
    private final MaturityEvaluationDateType maturityRepaymentEvaluationDateType;

    public ResponseMaturityRepaymentEvaluationDateDto(LocalDate maturityRepaymentEvaluationDate,
                                                      MaturityEvaluationDateType maturityRepaymentEvaluationDateType) {
        this.maturityRepaymentEvaluationDate = maturityRepaymentEvaluationDate;
        this.maturityRepaymentEvaluationDateType = maturityRepaymentEvaluationDateType;
    }
}
