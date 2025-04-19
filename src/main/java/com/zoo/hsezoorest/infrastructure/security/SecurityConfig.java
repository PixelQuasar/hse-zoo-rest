package com.zoo.hsezoorest.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] SWAGGER_WHITELIST = {
            "/zoo/swagger-ui.html",
            "/zoo/swagger-ui/**",
            "/zoo/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                
                        .anyRequest().authenticated()
                )
             
                .httpBasic(withDefaults())
              
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
} 