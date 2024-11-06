package com.wl2c.elswhereproductservice.domain.product.controller;

import com.wl2c.elswhereproductservice.domain.product.exception.OnSaleProductNotFoundException;
import com.wl2c.elswhereproductservice.domain.product.exception.TodayReceivedProductsNotFoundException;
import com.wl2c.elswhereproductservice.domain.product.model.dto.list.SummarizedProductDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.list.SummarizedProductForHoldingDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.request.RequestProductIdListDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.request.RequestProductSearchDto;
import com.wl2c.elswhereproductservice.domain.product.model.dto.response.*;
import com.wl2c.elswhereproductservice.domain.product.service.ProductEquityVolatilityService;
import com.wl2c.elswhereproductservice.domain.product.service.RepaymentEvaluationDatesService;
import com.wl2c.elswhereproductservice.domain.product.service.ProductService;
import com.wl2c.elswhereproductservice.domain.view.service.ViewService;
import com.wl2c.elswhereproductservice.global.model.dto.ResponsePage;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static java.lang.Long.parseLong;

@Tag(name = "상품", description = "상품 관련 api")
@RestController
@RequestMapping("/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final RepaymentEvaluationDatesService repaymentEvaluationDatesService;
    private final ProductEquityVolatilityService productEquityVolatilityService;
    private final ViewService viewService;

    /**
     * 청약 중인 상품 목록
     * <p>
     *     해당하는 정렬 타입(type)에 맞게 문자열을 기입해주세요.
     *
     *     최신순 : latest
     *     낙인순 : knock-in
     *     수익률순 : profit
     *     청약 마감일순 : deadline
     * </p>
     * <p>
     *     <br/>
     *     스텝다운 유형의 상품에 대해서 AI가 분석한 각 상품의 safetyScore를 제공합니다. <br/>
     *     스텝다운 유형이 아니거나 스텝다운 유형이지만 분석 정보가 없는 경우에는 null 값으로 제공됩니다. <br/>
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
     * AI가 추천하는 청약 중인 상품 목록
     * <p>
     *     safetyScore 내림차순으로 AI가 추천하는 청약 중인 상품 목록을 제공합니다.<br/>
     *     추천하는 조건에 만족하는 상품이 존재하지 않는다면 빈 리스트를 반환합니다.
     * </p>
     *
     * @return AI가 추천하는 청약 중인 상품 목록
     */
    @GetMapping("/on-sale/ai/recommendation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "AI가 추천하는 청약 중인 상품 목록",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SummarizedProductDto.class, type = "array"))),
            @ApiResponse(responseCode = "404", description = "청약 중인 상품이 존재하지 않습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OnSaleProductNotFoundException.class))),
    })
    public List<SummarizedProductDto> aiRecommendationListByOnSale() {
        return productService.aiRecommendationListByOnSale();
    }

    /**
     * 청약 종료된 상품 목록
     * <p>
     *     해당하는 정렬 타입(type)에 맞게 문자열을 기입해주세요.
     *
     *     최신순 : latest
     *     낙인순 : knock-in
     *     수익률순 : profit
     *     청약 마감일순 : deadline
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
     * 여러 상품 id로 해당 상품 리스트 조회
     * <p>
     *     참고 : user-service와 product-service 통신 간에 사용하고자 만든 API 입니다.
     * </p>
     *
     * @param requestProductIdListDto 조회하고자 하는 상품 id 리스트
     * @return 상품 리스트
     */
    @PostMapping("/list")
    public List<SummarizedProductDto> listByProductIds(@Valid @RequestBody RequestProductIdListDto requestProductIdListDto) {
        return productService.listByProductIds(requestProductIdListDto.getProductIdList());
    }

    /**
     * 여러 상품 id로 보유 상품에서 사용하기 위한 해당 상품 리스트 조회
     * <p>
     *     참고 : user-service와 product-service 통신 간에 사용하고자 만든 API 입니다.
     * </p>
     *
     * @param requestProductIdListDto 조회하고자 하는 상품 id 리스트
     * @return 보유 상품에서 사용하기 위한 정보를 담은 상품 리스트
     */
    @PostMapping("/holding/list")
    public List<SummarizedProductForHoldingDto> holdingListByProductIds(@Valid @RequestBody RequestProductIdListDto requestProductIdListDto) {
        return productService.holdingListByProductIds(requestProductIdListDto.getProductIdList());
    }

    /**
     * 상품 단건 조회
     * <p>
     *     maturityEvaluationDateType(만기 평가일 개수)을 제공하는 이유<br/>
     *     발행사에서 만기 평가일의 경우, 예정거래일 중 거래소 또는 관련 거래소가 개장하지 못하거나 시장교란 사유가 발생한 날을 대비하여 여러 만기 평가일을 제공하는 경우가 있습니다.<br/>
     *     실제 서버에서는 maturityEvaluationDate는 최초 만기 평가일 날짜만 가져오지만, 발행사가 만기 평가일을 여러 날로 설정했음을 나타내고자 해당 maturityRepaymentEvaluationDateType을 제공합니다.<br/><br/>
     *
     *     maturityRepaymentEvaluationDateType(만기상환평가일 타입)의 각 값들의 의미는 아래와 같습니다.
     *
     *     한 개의 만기 평가일 : SINGLE
     *     여러 개의 만기 평가일 : MULTIPLE
     *     파악되지 않음(서버에서 추가 파싱 필요 혹은 투자 설명서 오류) : UNKNOWN
     *
     * </p>
     *
     * @param id 조회할 상품 id
     * @return 상품 상세 정보
     */
    @GetMapping("/{id}")
    public ResponseSingleProductDto findOne(HttpServletRequest request,
                                            @PathVariable Long id) {
        viewService.view(id, parseLong(request.getHeader("requestId")));
        return productService.findOne(id, parseLong(request.getHeader("requestId")));
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

    /**
     * 원하는 조건에 대한 상품 검색
     * <p>
     *     <br/>
     *     아래 타입에 맞춰서 기입해주세요.
     *
     *     equityType(기초자산 유형) : 주가지수=INDEX, 종목=STOCK, 혼합=MIX
     *     type(상품 종류) : 스텝 다운=STEP_DOWN, 리자드 스텝 다운=LIZARD, 월지급식=MONTHLY_PAYMENT, 기타=ETC
     * </p>
     *
     * @param requestProductSearchDto 상품 검색 조건
     * @return 검색 조건에 맞는 상품 목록
     */
    @PostMapping("/search")
    public ResponsePage<SummarizedProductDto> searchProduct(@Valid @RequestBody RequestProductSearchDto requestProductSearchDto,
                                                            @ParameterObject Pageable pageable) {
        Page<SummarizedProductDto> result = productService.searchProduct(requestProductSearchDto, pageable);
        return new ResponsePage<>(result);
    }

    /**
     * 회차 번호에 해당하는 상품 검색
     * <p>
     *     각 발행사에서 회차는 유니크하지만, 다른 발행사끼리 회차 번호가 겹칠 수 있기때문에 리스트로 반환합니다.
     * </p>
     *
     * @param number 회차 번호
     * @return 검색 조건에 맞는 상품 리스트
     */
    @GetMapping("/search/{number}")
    public List<SummarizedProductDto> searchProductByIssueNumber(@PathVariable Integer number) {
        return productService.searchProductByIssueNumber(number);
    }

    /**
     * 오늘 받아온 상품들의 id 리스트 조회
     *
     * @return 오늘 받아온 상품들의 id 리스트
     */
    @GetMapping("/received/today")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = ResponseTodayReceivedProductIdsDto.class))}),
        @ApiResponse(responseCode = "404", description = "오늘 받아온 상품들이 존재하지 않습니다.",
            content = {@Content(schema = @Schema(implementation = TodayReceivedProductsNotFoundException.class))})
    })
    public ResponseTodayReceivedProductIdsDto findTodayReceivedProductIds() {
        return productService.findTodayReceivedProductIds();
    }

    /**
     * 특정 상품의 다음 상환평가일 조회
     * <p>
     *     조기상환평가일이 모두 지나면, 가장 마지막은 만기상환평가일을 보여줍니다.
     *     반환 값에 order는 몇 번째 상환평가일에 해당하는지의 차수를 의미합니다.
     * </p>
     *
     * @param id 상품 id
     * @return 다음 상환평가일
     */
    @GetMapping("/next/evaluation/{id}")
    public ResponseNextRepaymentEvaluationDateDto findNextRepaymentEvaluationDate(@PathVariable Long id) {
        return repaymentEvaluationDatesService.findNextRepaymentEvaluationDate(id);
    }

    /**
     * 특정 상품의 만기상환평가일 및 만기상환평가일 타입 조회
     * <p>
     *     maturityRepaymentEvaluationDateType(만기상환평가일 타입)의 각 값들의 의미는 아래와 같습니다.
     *
     *     한 개의 만기 평가일 : SINGLE
     *     여러 개의 만기 평가일 : MULTIPLE
     *     파악되지 않음(서버에서 추가 파싱 필요 혹은 투자 설명서 오류) : UNKNOWN
     * </p>
     *
     * @param id 상품 id
     * @return 만기상환평가일 및 만기상환평가일 타입 dto
     */
    @GetMapping("/maturity/evaluation/{id}")
    public ResponseMaturityRepaymentEvaluationDateDto findMaturityRepaymentEvaluationDate(@PathVariable Long id) {
        return repaymentEvaluationDatesService.findMaturityRepaymentEvaluationDate(id);
    }

    /**
     * 특정 상품의 기초자산별 변동성 조회
     *
     * @param id 상품 id
     * @return 기초자산명 및 해당 각 기초자산에 대한 변동성 dto
     */
    @GetMapping("/equity/volatility/{id}")
    public ResponseProductEquityVolatilityDto findProductEquityVolatilities(@PathVariable Long id) {
        return productEquityVolatilityService.findProductEquityVolatilities(id);
    }

    /**
     * 일일 인기 TOP5 상품 리스트 조회
     *
     * @return 좋아요 증감 + 조회수가 높은 상품 정보 리스트 반환(개수 중복 고려)
     */
    @GetMapping("/hot/daily")
    public List<SummarizedProductDto> getDailyTop5Products() {
        return productService.getDailyTop5Products();
    }
}
