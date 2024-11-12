package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeRequests(authz -> authz
                        .requestMatchers("/assets/**", "/index.html").permitAll() // Dozvoli pristup statičkim datotekama
                        .requestMatchers("/login", "/signup").permitAll()        // Dopuštenje za pristup javnim stranicama
                        .anyRequest().authenticated()                           // Zahtijevaj autentifikaciju za ostale rute
                )
                .formLogin(login -> login
                        .loginPage("/login").permitAll()
                )
                .build();
    }
}


