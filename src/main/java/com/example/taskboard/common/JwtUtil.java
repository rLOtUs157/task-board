package com.example.taskboard.common;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT令牌工具：生成token、校验token、解析用户名
 */
@Component
public class JwtUtil {

    // 签名密钥（生产应放配置）
    private final SecretKey key = Keys.hmacShaKeyFor(
            "task-board-secret-key-0123456789-abcdefghijklmn".getBytes(StandardCharsets.UTF_8));

    // 过期时间：24小时
    private final long expire = 1000L * 60 * 60 * 24;

    // 生成token
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expire))
                .signWith(key)
                .compact();
    }

    // 校验token是否有效
    public boolean validate(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 从token解析用户名
    public String getUsername(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }
}
