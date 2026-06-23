package com.stationery.inventory.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component   //registers this class as a spring bean so it can be injected into JwtFilter 
public class JwtUtil {

    @Value("${jwt.secret}")  //read the property jwt.secret from application.yml and inject into variable below
    private String secret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());  //converts plain-text secret string into a secure Cryptographic HMAC key object required by JWT parsing library to verify token signature.
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();       //extracts email from payload
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);  //extracts role from payload
    }

    public boolean isTokenValid(String token) {
        try {
            getClaims(token);                   //check expired or valid
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    //gives a java object containing user's details.
}