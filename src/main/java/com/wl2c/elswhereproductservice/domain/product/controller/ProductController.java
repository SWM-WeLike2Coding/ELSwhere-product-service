package com.wl2c.elswhereproductservice.domain.product.controller;

import com.wl2c.elswhereproductservice.domain.product.model.dto.list.SummarizedProductDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.response.ResponseProductComparisonTargetDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.response.ResponseSingleProductDto;
import com.wl2c.elswhereproductservice.domain.product.service.ProductService;
import com.wl2c.elswhereproductservice.global.model.dto.ResponsePage;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "상품", description = "상품 관련 api")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 청약 중인 상품 목록
     * <p>
     *     해당하는 정렬 타입(type)에 맞게 문자열을 기입해주세요.
     *     (인기순은 좋아요 작업 후, 기능 추가 예정)
     *
     *     최신순 : latest
     *     낙인순 : knock-in
     *     수익률순 : profit
     * </p>
     *
     * @param type 정렬 타입
     * @return 페이징된 청약 중인 상품 목록
     */
    @GetMapping("/on-sale")
    public ResponsePage<SummarizedProductDto> listByOnSale(@RequestParam(name = "type") String type,
                                                            @ParameterObject Pageable pageable) {
        Page<SummarizedProductDto> result = productService.listByOnSale(type, pageable);
        return new ResponsePage<>(result);
    }

    /**
     * 청약 종료된 상품 목록
     * <p>
     *     해당하는 정렬 타입(type)에 맞게 문자열을 기입해주세요.
     *     (인기순은 좋아요 작업 후, 기능 추가 예정)
     *
     *     최신순 : latest
     *     낙인순 : knock-in
     *     수익률순 : profit
     * </p>
     *
     * @param type 정렬 타입
     * @return 페이징된 청약 종료된 상품 목록
     */
    @GetMapping("/end-sale")
    public ResponsePage<SummarizedProductDto> listByEndSale(@RequestParam(name = "type") String type,
                                                            @ParameterObject Pageable pageable) {
        Page<SummarizedProductDto> result = productService.listByEndSale(type, pageable);
        return new ResponsePage<>(result);
    }

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

    /**
     * 특정 상품과 같은 기초자산의 상품들 리스트
     *
     * @param id 비교할 대상의 상품 id
     * @return map 형태의 비교할 대상(target)의 정보와 비교된 상품 리스트들(results) 반환
     */
    @GetMapping("/similar/{id}")
    public Map<String, List<ResponseProductComparisonTargetDto>> findComparisonTargets(@PathVariable Long id) {
        return productService.findComparisonTargets(id);
    }

}
