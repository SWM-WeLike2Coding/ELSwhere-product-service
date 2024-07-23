package com.wl2c.elswhereproductservice.domain.product.controller;

import com.wl2c.elswhereproductservice.domain.product.model.dto.response.ResponseIssuerDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.response.ResponseTickerSymbolDto;
import com.wl2c.elswhereproductservice.domain.product.service.IssuerService;
import com.wl2c.elswhereproductservice.domain.product.service.TickerSymbolService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "상품 기타", description = "상품 관련 기타 api")
@RestController
@RequestMapping("/others")
@RequiredArgsConstructor
public class ProductOthersController {

    private final TickerSymbolService tickerSymbolService;
    private final IssuerService issuerService;

    /**
     * 각 종목별 티커 심볼 리스트(중복 포함)
     */
    @GetMapping("/ticker")
    public List<ResponseTickerSymbolDto> listTickerSymbol() {
        return tickerSymbolService.listTickerSymbol();
    }

    /**
     * 모든 발행사 리스트
     */
    @GetMapping("/issuer")
    public List<ResponseIssuerDto> listIssuer() {
        return issuerService.listIssuer();
    }
}
