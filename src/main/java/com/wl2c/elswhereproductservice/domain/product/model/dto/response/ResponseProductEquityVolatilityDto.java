package com.wl2c.elswhereproductservice.domain.product.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
public class ResponseProductEquityVolatilityDto {

    @Schema(description = "기초자산명 리스트")
    private final List<String> equityList;

    @Schema(description = "각 기초자산 별 티커 정보")
    private final Map<String, BigDecimal> equityVolatilities;

    public ResponseProductEquityVolatilityDto(List<String> equityList,
                                              Map<String, BigDecimal> equityVolatilities) {
        this.equityList = equityList;
        this.equityVolatilities = equityVolatilities;
    }
}
