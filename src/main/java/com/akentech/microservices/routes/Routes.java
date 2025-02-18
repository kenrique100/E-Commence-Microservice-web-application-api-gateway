package com.akentech.microservices.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Routes {

    @Bean
    public RouteLocator productServiceRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r.path("/api/product/**")
                        .uri("http://localhost:8080"))
                .build();
    }
    @Bean
    public RouteLocator orderServiceRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("order-service", r -> r.path("/api/order/**")
                        .uri("http://localhost:8085"))
                .build();
    }
    @Bean
    public RouteLocator inventoryServiceRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("inventory-service", r -> r.path("/api/inventory/**")
                        .uri("http://localhost:8087"))
                .build();
    }
}
