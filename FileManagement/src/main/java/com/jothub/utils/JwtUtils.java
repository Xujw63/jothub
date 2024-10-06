package com.jothub.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtUtils {

    // 使用更安全的密钥生成方式
    private static final Key signKey = Keys.hmacShaKeyFor("itheima-should-be-at-least-256-bits-long".getBytes());
    private static final Long expire = 43200000L; // 12小时

    /**
     * 生成JWT令牌
     * @param claims JWT第二部分负载 payload 中存储的内容
     * @return 生成的JWT
     */
    public static String generateJwt(Map<String, Object> claims) {
        return Jwts.builder()
                .addClaims(claims)
                .signWith(signKey, SignatureAlgorithm.HS256)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
    }

    /**
     * 解析JWT令牌
     * @param jwt JWT令牌
     * @return JWT第二部分负载 payload 中存储的内容
     */
    public static Claims parseJWT(String jwt) {
        return Jwts.parser()                    // 使用 parser
                .setSigningKey(signKey)                 // 设置签名密钥
                .build()                                // 构建解析器
                .parseClaimsJws(jwt)                    // 解析并验证JWT
                .getBody();                             // 获取负载体（claims）
    }
}

