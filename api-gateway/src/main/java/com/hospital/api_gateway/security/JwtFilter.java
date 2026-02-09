package com.hospital.api_gateway.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;



@Component
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {

    // Ideally, load this from application.yml using @Value
    private final String SECRET = "HOSPITAL_SECRET_KEY_HOSPITAL_SECRET_KEY";

    public JwtFilter() {
        super(Config.class);
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
                        .setSigningKey(SECRET.getBytes())
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

