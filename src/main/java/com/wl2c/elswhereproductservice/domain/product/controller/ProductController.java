package com.wl2c.elswhereproductservice.domain.product.controller;

import com.wl2c.elswhereproductservice.domain.product.model.dto.response.ResponseSingleProductDto;
import com.wl2c.elswhereproductservice.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 단건 조회
     *
     * @param id 조회할 상품 id
     * @return 상품 상세 정보
     */
    @GetMapping("/{id}")
    public ResponseSingleProductDto findOne(@PathVariable Long id) {
        return productService.findOne(id);
    }

}
