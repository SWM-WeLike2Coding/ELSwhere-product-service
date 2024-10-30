package com.wl2c.elswhereproductservice.client.analysis.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ResponseAIResultDto {

    @Schema(description = "AI 결과 id", example = "1")
    private final Long AIResultId;

    @Schema(description = "상품 id", example = "1")
    private final Long productId;

    @Schema(description = "AI가 판단한 스텝다운 상품 안전도", example = "0.76")
    private final BigDecimal safetyScore;

}
