package com.rental.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    @Value("${spring.security.jwt.secret}")
    private String secret;
    
    @Value("${spring.security.jwt.expiration}")
    private Long expiration;
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String generateToken(String email, Integer userId, String roleName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("roleName", roleName);
        return createToken(claims, email);
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Integer extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Integer.class);
    }
    
    public String extractRoleName(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roleName", String.class);
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public Boolean validateToken(String token, String email) {
        final String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }
    
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            boolean isValid = !isTokenExpired(token);
            if (!isValid) {
                logger.debug("Token validation failed: Token expired");
            }
            return isValid;
        } catch (Exception e) {
            logger.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}

