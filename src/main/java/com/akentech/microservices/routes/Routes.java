package com.akentech.microservices.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Routes {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r.path("/api/product/**")
                        .uri("http://localhost:8080"))
                .route("product-service-swagger", r -> r.path("/aggregate/product-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/aggregate/product-service/v3/api-docs", "/v3/api-docs"))
                        .uri("http://localhost:8080"))
                .route("order-service", r -> r.path("/api/order/**")
                        .uri("http://localhost:8085"))
                .route("order-service-swagger", r -> r.path("/aggregate/order-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/aggregate/order-service/v3/api-docs", "/v3/api-docs"))
                        .uri("http://localhost:8085"))
                .route("inventory-service", r -> r.path("/api/inventory/**")
                        .uri("http://localhost:8087"))
                .route("inventory-service-swagger", r -> r.path("/aggregate/inventory-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/aggregate/inventory-service/v3/api-docs", "/v3/api-docs"))
                        .uri("http://localhost:8087"))
                .build();
    }
}