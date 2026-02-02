package com.hospital.doctor_service.config;

import com.hospital.doctor_service.security.JwtAuthenticationFilter;
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

                // Gateway එකෙන් එන Role එක පරීක්ෂා කරන Filter එක මෙහිදී එකතු කරයි
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Role-based Access Control (RBAC)
                        .requestMatchers(HttpMethod.POST, "/api/v1/doctors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/doctors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/doctors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/doctors/**").hasAnyRole("ADMIN", "STAFF", "DOCTOR","PATIENT")

                        //අනෙක් සියලුම ඉල්ලීම් වලට අවසර අවශ්‍යයි
                        .anyRequest().authenticated()
                )


                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // JWT Filter will be added here in next steps

        return http.build();
    }

}
