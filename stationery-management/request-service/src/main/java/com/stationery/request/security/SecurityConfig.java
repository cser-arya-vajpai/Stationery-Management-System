package com.stationery.request.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration  //this is a configuration Class 
@EnableWebSecurity   //Enable spring security web support 
@RequiredArgsConstructor //injects jwtFilter constructor here
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/requests").hasRole("STUDENT")
                .requestMatchers(HttpMethod.GET, "/api/requests/my").hasRole("STUDENT")
                .requestMatchers(HttpMethod.GET, "/api/requests").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/requests/*/status").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);  //registers our custom jwtFilter to execute before Spring Security's default UsernamePasswordAuthenticationFilter. Ensures authentication is setup before the security rules checks are run

        return http.build();
    }
}