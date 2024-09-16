package com.wl2c.elswhereproductservice.client.user.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/v1/product/like/{productId}")
    void checkIsLiked(@RequestHeader("requestId") String requestId, @PathVariable Long productId);
}
