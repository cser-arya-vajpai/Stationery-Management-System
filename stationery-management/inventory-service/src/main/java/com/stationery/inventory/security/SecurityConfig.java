package com.stationery.inventory.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration    //This class contains @Bean configuration definitions
@EnableWebSecurity   //enables spring security's web security support
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean  //register the SecurityFilterChain as a bean in Application Context
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()    //anyone can access actuator URL
                .requestMatchers(HttpMethod.GET, "/api/inventory/**").authenticated()   //any authenticated user can access inventory and execute GET requests
                .requestMatchers(HttpMethod.POST, "/api/inventory/**").hasRole("ADMIN")    //authenticated + admin -> post
                .requestMatchers(HttpMethod.PUT, "/api/inventory/**").hasRole("ADMIN")     //authenticated + admin -> put
                .requestMatchers(HttpMethod.DELETE, "/api/inventory/**").hasRole("ADMIN")  //authenticated + admin -> delete
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}