package com.hospital.patient_service.config;

import com.hospital.patient_service.security.JwtAuthenticationFilter;
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
        http.csrf(csrf -> csrf.disable())
                // Gateway එකෙන් එන JWT එක පරීක්ෂා කරන Filter එක
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // රෝගියෙකු සෑදීමට අවසර (Registration normally via RabbitMQ, but Admin might need manual POST)
                        .requestMatchers(HttpMethod.POST, "/api/v1/patients/**").hasRole("ADMIN")

                        // රෝගියාට තමන්ගේ තොරතුරු Update කිරීමට අවසර ලබා දෙයි
                        .requestMatchers(HttpMethod.PUT, "/api/v1/patients/**").hasAnyRole("ADMIN", "PATIENT")

                        // පරීක්ෂා කිරීම සඳහා සැමට අවසර (Doctor/Staff/Admin/Patient)
                        .requestMatchers(HttpMethod.GET, "/api/v1/patients/**").hasAnyRole("ADMIN", "DOCTOR", "STAFF", "PATIENT")

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/patients/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}