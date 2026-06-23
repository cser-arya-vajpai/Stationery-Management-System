package com.stationery.request.security;

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
//request interceptor or the SECURITY GUARD

@Component   //marked as spring bean to b injected elsewhere
@RequiredArgsConstructor  //constructor of jwtUtil can be injected here
public class JwtFilter extends OncePerRequestFilter {  //spring class that guarantees that filter executes exactly once per HTTP request

    private final JwtUtil jwtUtil;

    //Servlet Filter = security guard standing at entrance of web server
    @Override
    protected void doFilterInternal(HttpServletRequest request,  //it is a servlet container that contains all the details of incoming HTTP requests.
                                    HttpServletResponse response,  //represents outgoing HTTP response 
                                    FilterChain filterChain)   //represents chain of other filters that must pass through before it can reach the controller
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");  //we extract HTTP header named "Authorization"

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.isTokenValid(token)) {
                String email = jwtUtil.extractEmail(token);
                String role = jwtUtil.extractRole(token);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}