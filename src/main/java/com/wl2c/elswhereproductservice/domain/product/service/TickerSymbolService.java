package com.wl2c.elswhereproductservice.domain.product.service;

import com.wl2c.elswhereproductservice.domain.product.model.dto.response.ResponseTickerSymbolDto;
import com.wl2c.elswhereproductservice.domain.product.model.entity.TickerSymbol;
import com.wl2c.elswhereproductservice.domain.product.repository.TickerSymbolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TickerSymbolService {
    private final TickerSymbolRepository tickerSymbolRepository;

    public List<ResponseTickerSymbolDto> listTickerSymbol() {
        List<TickerSymbol> tickerSymbolList = tickerSymbolRepository.findAll();
        return tickerSymbolList.stream()
                .map(ResponseTickerSymbolDto::new)
                .collect(Collectors.toList());
    }
}
