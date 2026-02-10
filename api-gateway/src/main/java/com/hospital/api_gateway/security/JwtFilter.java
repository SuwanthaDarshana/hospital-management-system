package com.hospital.api_gateway.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;



@Component
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {


    private final String secret;

    public JwtFilter(@Value("${jwt.secret}") String secret) {
        super(Config.class);
        this.secret = secret;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 1. Check for Authorization Header
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // 2. Extract Token
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.replace("Bearer ", "");

            // 3. Validate Token & Extract Claims
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secret.getBytes())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                // 4. Pass Data to Microservices (Optional but good practice)
                // Even though downstream services re-validate, these headers are useful for logging
                String role = claims.get("role", String.class);
                String email = claims.getSubject();

                exchange.getRequest().mutate()
                        .header("X-User-Role", role)
                        .header("X-User-Email", email)
                        .build();

            } catch (Exception e) {
                // If token is expired or invalid, block request here
                System.out.println("Invalid Token Access Attempt");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {}
}

