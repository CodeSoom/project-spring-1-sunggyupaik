package com.example.bookclub.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String encode(Long id, String email) {
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Date ext = new Date();
        long expiredTime = 1000 * 60 * 60 * 2L;
        ext.setTime(ext.getTime() + expiredTime);

        return Jwts.builder()
                .setHeader(header)
                .setSubject(email)
                .claim("userId", id)
                .setExpiration(ext)
                .signWith(key)
                .compact();
    }

    public Claims decode(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
