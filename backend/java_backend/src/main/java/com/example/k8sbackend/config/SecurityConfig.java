package com.example.k8sbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Most permissive rules should come first
                .requestMatchers(
                    "/api/public/**",  // Notice the /** to match all sub-paths
                    "/api/public/status"  // Explicitly include the exact path too
                ).permitAll()
                // Then your login endpoints
                .requestMatchers(
                    "/api/login",
                    "/api/physical/login", 
                    "/api/machine/login"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}