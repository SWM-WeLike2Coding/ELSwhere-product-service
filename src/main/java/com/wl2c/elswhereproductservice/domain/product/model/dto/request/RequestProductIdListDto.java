package com.wl2c.elswhereproductservice.domain.product.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class RequestProductIdListDto {

    @Schema(description = "상품 id 리스트", example = "[3, 6, 9, 12]")
    private final List<Long> productIdList;

    @JsonCreator
    public RequestProductIdListDto(@JsonProperty("productIdList") List<Long> productIdList) {
        this.productIdList = productIdList;
    }
}
