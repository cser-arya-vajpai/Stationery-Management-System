package com.stationery.request.security;

import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component   //marks this class as a spring bean so it can be injected elsewhere
public class JwtUtil {

    @Value("${jwt.secret}")  //we are taking secret from application.yml and injecting it into our variable
    private String secret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());   //converts the raw bytes of your secret key string into a cryptographic key object

    //extracting email from JWT payload using Claims(helper class to do the same)
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    //extracting role from JWT payload 
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    //Checking if token is expired or valid
    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    //It builds a JWT parser that decrypts and reads the token, verifies the signature using signing key and parses token's JSON payload and extracts claims
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}