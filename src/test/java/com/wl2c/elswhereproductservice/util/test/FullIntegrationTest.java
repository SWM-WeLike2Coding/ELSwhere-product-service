package com.wl2c.elswhereproductservice.util.test;

import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.lang.annotation.*;

/**
 * local환경에서만 실행되는 테스트임을 명시합니다. 이 annotation이 붙어있으면, Build/CI/CD시에 테스트가 수행되지 않습니다.
 * 통합 테스트는 local에서만 테스트합니다. (매번 SpringBootTest하기에는 속도가 너무 느리고 환경 구성의 어려움)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "local")
public @interface FullIntegrationTest {
}
