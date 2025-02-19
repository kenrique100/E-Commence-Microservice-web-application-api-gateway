package com.akentech.microservices.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@Configuration
public class Routes {

    private static final Logger logger = LoggerFactory.getLogger(Routes.class);

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        logger.info("Configuring routes with circuit breakers...");
        return builder.routes()
                .route("product-service", r -> r.path("/api/product/**")
                        .filters(f -> f.circuitBreaker(config -> config
                                .setName("productServiceCircuitBreaker")
                                .setFallbackUri("forward:/fallback")))
                        .uri("http://localhost:8080"))
                .route("product-service-swagger", r -> r.path("/aggregate/product-service/v3/api-docs")
                        .filters(f -> {
                            logger.info("Configuring circuit breaker for product-service-swagger");
                            return f.rewritePath("/aggregate/product-service/v3/api-docs", "/v3/api-docs")
                                    .circuitBreaker(config -> config
                                            .setName("productServiceCircuitBreaker")
                                            .setFallbackUri("forward:/fallback"));
                        })
                        .uri("http://localhost:8080"))
                .route("order-service", r -> r.path("/api/order/**")
                        .filters(f -> f.circuitBreaker(config -> config
                                .setName("orderServiceCircuitBreaker")
                                .setFallbackUri("forward:/fallback")))
                        .uri("http://localhost:8085"))
                .route("order-service-swagger", r -> r.path("/aggregate/order-service/v3/api-docs")
                        .filters(f -> {
                            logger.info("Configuring circuit breaker for order-service-swagger");
                            return f.rewritePath("/aggregate/order-service/v3/api-docs", "/v3/api-docs")
                                    .circuitBreaker(config -> config
                                            .setName("orderServiceCircuitBreaker")
                                            .setFallbackUri("forward:/fallback"));
                        })
                        .uri("http://localhost:8085"))
                .route("inventory-service", r -> r.path("/api/inventory/**")
                        .filters(f -> f.circuitBreaker(config -> config
                                .setName("inventoryServiceCircuitBreaker")
                                .setFallbackUri("forward:/fallback")))
                        .uri("http://localhost:8087"))
                .route("inventory-service-swagger", r -> r.path("/aggregate/inventory-service/v3/api-docs")
                        .filters(f -> {
                            logger.info("Configuring circuit breaker for inventory-service-swagger");
                            return f.rewritePath("/aggregate/inventory-service/v3/api-docs", "/v3/api-docs")
                                    .circuitBreaker(config -> config
                                            .setName("inventoryServiceCircuitBreaker")
                                            .setFallbackUri("forward:/fallback"));
                        })
                        .uri("http://localhost:8087"))
                .build();
    }

    @Bean
    public RouteLocator fallbackRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("fallbackRoute", r -> r.path("/fallback")
                        .filters(f -> f.setStatus(HttpStatus.SERVICE_UNAVAILABLE)
                                .modifyResponseBody(String.class, String.class, (exchange, s) -> {
                                    return Mono.just("{\"message\": \"Service is currently unavailable. Please try again later.\"}");
                                }))
                        .uri("no://op")) // Placeholder URI
                .build();
    }
}