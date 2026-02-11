package com.hospital.patient_service.config;

import com.hospital.patient_service.security.GatewayHeaderAuthFilter;
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
                        .requestMatchers(HttpMethod.POST, "/api/v1/patients/**").hasRole("STAFF")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/patients/**").hasAnyRole("STAFF", "PATIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/patients/**").hasAnyRole("ADMIN", "DOCTOR", "STAFF", "PATIENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/patients/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}