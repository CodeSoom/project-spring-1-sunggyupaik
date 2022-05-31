package com.example.bookclub.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 토큰을 이용하여 인코딩, 디코딩한다.
 */
@Component
public class JwtUtil {
    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 주어진 사용자 식별자와 사용자 이메일로 인코딩 된 토큰을 생성하고 반환한다.
     *
     * @param id 사용자 식별자
     * @param email 사용자 이메일
     * @return 인코딩 된 토큰
     */
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

    /**
     * 주어진 토큰을 디코딩하여 사용자 정보를 반환한다.
     *
     * @param token 토큰
     * @return 디코딩 된 사용자 정보
     */
    public Claims decode(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
