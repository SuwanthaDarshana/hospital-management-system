package com.hospital.api_gateway.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;



@Component
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config>{

    private final String SECRET = "HOSPITAL_SECRET_KEY_HOSPITAL_SECRET_KEY";

    public JwtFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();

            if (path.startsWith("/auth")){
                return chain.filter(exchange);
            }



            if (!exchange.getRequest().getHeaders().containsKey("Authorization")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = exchange.getRequest().getHeaders().get("Authorization").get(0);

            try {
                token = token.replace("Bearer ", "");
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(SECRET.getBytes())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                // 3. Token එකේ ඇති Role එක කියවා 'role' ලෙස header එකට එක් කිරීම
                String role = claims.get("role").toString();
                String email = claims.getSubject(); // JWT subject = email

                exchange.getRequest().mutate()
                        .header("X-User-Role", role)
                        .header("X-User-Email", email)
                        .build();
            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);

        };
    }

    public static class Config {}
}
