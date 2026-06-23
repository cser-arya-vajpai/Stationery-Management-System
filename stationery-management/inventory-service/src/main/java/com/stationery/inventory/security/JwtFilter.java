package com.stationery.inventory.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component    //generic bean
@RequiredArgsConstructor    //bean of util to be injected here

//! OncePerRequestFilter = spring base class ensuring that this filter is executed exactly once per HTTP request
public class JwtFilter extends OncePerRequestFilter {   

    private final JwtUtil jwtUtil;   //declares dependency on our util class

    @Override
    protected void doFilterInternal(HttpServletRequest request,   //incoming request data (header, URL, body)
                                    HttpServletResponse response, //used if we want to send an error message
                                    FilterChain filterChain)      //once done with checks, call filterChain.doFilter to let request continue to next filter
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");  //reading value of header that's named "authorization"

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.isTokenValid(token)) {
                String email = jwtUtil.extractEmail(token);
                String role = jwtUtil.extractRole(token);      //extracting email, role if token is valid and authHeader isn't null and header starts with bearer

                UsernamePasswordAuthenticationToken authentication =         //creates a spring security authentication token
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );  //basically, this method is creating an ID card that spring security can understand
                SecurityContextHolder.getContext().setAuthentication(authentication);  //saving authentication token into Spring's security context.
            }
        }
        filterChain.doFilter(request, response);  //passing the request to next filter.
    }
}