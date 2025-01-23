package com.example.demo.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final String secretkey;
    private static final long EXPIRATION_TIME = 30 * 60 * 60 * 1000; // 30 hours

    public JwtService() {
        this.secretkey = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
    }

    public String generateToken(String username, String role, String userID) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("userID", userID);
        return "<CustomJWT>" + Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        if(token.startsWith("<CustomJWT>"))
            token = token.substring("<CustomJWT>".length());
        System.out.println("Role korisnika: " + extractClaim(token, claims -> claims.get("role", String.class)));
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public String extractUserID(String token) {
        if(token.startsWith("<CustomJWT>"))
            token = token.substring("<CustomJWT>".length());
        return extractClaim(token, claims -> claims.get("userID", String.class));
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        if(token.startsWith("<CustomJWT>"))
            token = token.substring("<CustomJWT>".length());
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        if(token.startsWith("<CustomJWT>"))
            token = token.substring("<CustomJWT>".length());
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        if(token.startsWith("<CustomJWT>"))
            token = token.substring("<CustomJWT>".length());
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        if(token.startsWith("<CustomJWT>"))
            token = token.substring("<CustomJWT>".length());
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretkey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
