package com.akentech.microservices.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    // Whitelisted URLs that do not require authentication
    private final String[] freeResourceUrls = {
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
            "/swagger-resources/**", "/api-docs/**", "/aggregate/**"
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        // Allow access to free resources without authentication
                        .pathMatchers(freeResourceUrls).permitAll()
                        // Secure all other endpoints
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                // Disable CSRF for stateless APIs
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Extract roles from the JWT claim
            Collection<String> authorities = jwt.getClaimAsStringList("roles"); // Adjust claim as needed
            return Flux.fromIterable(authorities == null ? List.of() :
                    authorities.stream()
                            .map(role -> (GrantedAuthority) () -> "ROLE_" + role) // Prefix roles with "ROLE_"
                            .collect(Collectors.toList()));
        });
        return converter;
    }
}