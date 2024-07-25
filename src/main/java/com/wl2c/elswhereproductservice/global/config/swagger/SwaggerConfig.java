package com.wl2c.elswhereproductservice.global.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;


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
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER).name("Authorization");
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .security(Collections.singletonList(securityRequirement))
                .addServersItem(new Server().url(developmentServerUrl).description("개발 서버"))
                .addServersItem(new Server().url(localServerUrl).description("로컬 서버"));
    }

}
