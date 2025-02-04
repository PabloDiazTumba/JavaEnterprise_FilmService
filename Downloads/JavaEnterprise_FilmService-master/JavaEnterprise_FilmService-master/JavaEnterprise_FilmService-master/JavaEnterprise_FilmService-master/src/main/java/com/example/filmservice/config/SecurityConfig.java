package com.example.filmservice.config;

import com.example.filmservice.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(@Lazy CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().ignoringRequestMatchers("/api/auth/register", "/api/auth/login", "/h2-console/**") // Ignorera CSRF för dessa endpoints, inklusive H2-konsolen
                .and()
                .authorizeRequests()
                .requestMatchers("/api/auth/register", "/api/auth/login", "/h2-console/**").permitAll()  // Tillåt utan autentisering för H2
                .anyRequest().authenticated()  // Alla andra endpoints kräver autentisering
                .and()
                .formLogin()
                .defaultSuccessUrl("/home", true)  // Omdirigerar användaren till "/home"
                .permitAll()
                .and()
                .logout().permitAll();

        // Tillåt att vi öppnar H2-konsolen i en webbläsare
        http.headers().frameOptions().sameOrigin(); // Tillåt visning av H2-konsolen i en iframe

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}