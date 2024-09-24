package com.wl2c.elswhereproductservice.domain.product.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class ResponseTodayReceivedProductIdsDto {
    @Schema(description = "상품 id 리스트", example = "[3, 6, 9, 12]")
    private final List<Long> productIdList;

    public ResponseTodayReceivedProductIdsDto(List<Long> productIdList) {
        this.productIdList = productIdList;
    }
}
