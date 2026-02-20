package com.hospital.doctor_service.config;

import com.hospital.doctor_service.security.GatewayHeaderAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new GatewayHeaderAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(HttpMethod.POST, "/api/v1/doctors/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/v1/doctors/**").hasAnyRole("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/doctors/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/v1/doctors/**").hasAnyRole("ADMIN", "STAFF", "DOCTOR", "PATIENT")
                                .requestMatchers(
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**"
                                ).permitAll()
                                .anyRequest().authenticated()

                );

        return http.build();
    }
}