package com.wl2c.elswhereproductservice.client.analysis.api;

import com.wl2c.elswhereproductservice.client.analysis.dto.response.ResponseAIResultDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.request.RequestProductIdListDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "analysis-service")
public interface AnalysisServiceClient {

    @PostMapping("/v1/ai/list")
    List<ResponseAIResultDto> getAIResultList(@Valid @RequestBody RequestProductIdListDto requestProductIdListDto);

}
