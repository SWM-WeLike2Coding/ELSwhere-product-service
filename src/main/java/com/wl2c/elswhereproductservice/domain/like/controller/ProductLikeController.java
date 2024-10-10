package com.wl2c.elswhereproductservice.domain.like.controller;

import com.wl2c.elswhereproductservice.domain.like.model.dto.response.ResponseLikeCountDto;
import com.wl2c.elswhereproductservice.domain.like.service.LikeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static java.lang.Long.parseLong;

@Tag(name = "상품 좋아요", description = "상품 좋아요 관련 api")
@RestController
@RequestMapping("/v1/product")
@RequiredArgsConstructor
public class ProductLikeController {

    private final LikeService likeService;

    /**
     * 상품에 좋아요 표시
     * 중복으로 좋아요 표시해도 1개만 적용됩니다.
     *
     * @param id 상품 id
     */
    @PostMapping("/like/{id}")
    public ResponseLikeCountDto like(HttpServletRequest request, @PathVariable Long id) {
        likeService.like(id, parseLong(request.getHeader("requestId")));
        return new ResponseLikeCountDto(likeService.getCountOfLikes(id));
    }

    /**
     * 좋아요 취소
     * 중복으로 좋아요 취소해도 최초 1건만 적용됩니다.
     *
     * @param id 상품 id
     */
    @DeleteMapping("/like/{id}")
    public ResponseLikeCountDto cancelLike(HttpServletRequest request, @PathVariable Long id) {
        likeService.cancelLike(id, parseLong(request.getHeader("requestId")));
        return new ResponseLikeCountDto(likeService.getCountOfLikes(id));
    }

    /**
     * 레디스에 있는 특정 사용자에 대한 좋아요 기록 DB에 dump (프론트 사용x)
     */
    @PostMapping("/like/dump")
    public void dumpLike(HttpServletRequest request) {
        likeService.dumpToDbForUserId(parseLong(request.getHeader("requestId")));
    }
}
