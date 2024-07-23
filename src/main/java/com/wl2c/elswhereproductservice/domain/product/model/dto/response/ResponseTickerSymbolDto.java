package com.wl2c.elswhereproductservice.domain.product.model.dto.response;

import com.wl2c.elswhereproductservice.domain.product.model.entity.TickerSymbol;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ResponseTickerSymbolDto {

    @Schema(description = "티커 심볼 id", example = "1")
    private final Long id;

    @Schema(description = "기초자산 명", example = "Alphabet Inc.")
    private final String equityName;

    @Schema(description = "티커 심볼", example = "GOOGL")
    private final String tickerSymbol;

    public ResponseTickerSymbolDto(TickerSymbol tickerSymbol) {
        this.id = tickerSymbol.getId();
        this.equityName = tickerSymbol.getEquityName();
        this.tickerSymbol = tickerSymbol.getTickerSymbol();
    }
}
