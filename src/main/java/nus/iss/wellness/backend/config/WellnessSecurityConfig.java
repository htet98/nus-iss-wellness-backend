package nus.iss.wellness.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

//Loh Si Hua - 27 Jun 2026
// @Configuration — disabled: SecurityConfig now handles all auth with JWT filter
// @Order(1)
public class WellnessSecurityConfig {

    @Bean
    public SecurityFilterChain wellnessFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        return http.build();
    }
}

// Spring Security was blocking all API requests by default, 
// causing the repeated 401 Unauthorized errors in Postman. 
// Since we couldn't modify SecurityConfig.java or application.properties, 
// I created this new file to disable authentication specifically for /api/** routes. 
// This allows Postman to call the endpoints without needing a username and password.
