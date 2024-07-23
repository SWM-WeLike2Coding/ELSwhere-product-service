package com.wl2c.elswhereproductservice.domain.product.service;

import com.wl2c.elswhereproductservice.domain.product.model.dto.response.ResponseIssuerDto;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Issuer;
import com.wl2c.elswhereproductservice.domain.product.repository.IssuerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IssuerService {

    private final IssuerRepository issuerRepository;

    public List<ResponseIssuerDto> listIssuer() {
        List<Issuer> issuerList = issuerRepository.findAllByIssuerState();
        return issuerList.stream()
                .map(ResponseIssuerDto::new)
                .collect(Collectors.toList());
    }
}
