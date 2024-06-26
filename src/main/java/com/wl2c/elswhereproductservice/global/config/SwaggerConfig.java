package com.wl2c.elswhereproductservice.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
@OpenAPIDefinition(
        info = @Info(title = "API Document",
                description = "PRODUCT SERVICE 명세서",
                version = "v1.0.0"
        )
)
public class SwaggerConfig {

    @Value("${server.url.development}")
    private String developmentServerUrl;

    @Value("${server.url.local}")
    private String localServerUrl;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url(developmentServerUrl).description("개발 서버"))
                .addServersItem(new Server().url(localServerUrl).description("로컬 서버"));
    }

}
