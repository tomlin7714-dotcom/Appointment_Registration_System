package com.appointment.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // 暂时硬编码，避免配置文件读取问题
    private String secret = "appointment_secret_key";
    private long expiration = 7200000;

    // 生成Token
    public String generateToken(Integer userId, Integer role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    // 解析Token
    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    // 从Token中获取用户ID
    public Integer getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return (Integer) claims.get("userId");
    }

    // 从Token中获取用户角色
    public Integer getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return (Integer) claims.get("role");
    }

    // 验证Token是否过期
    public boolean isTokenExpired(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().before(new Date());
    }
}