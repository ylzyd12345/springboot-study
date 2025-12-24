package com.kev1n.spring4demo.common.util;

import com.kev1n.spring4demo.common.constant.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
public class JwtUtils {
    
    private static final SecretKey KEY = Keys.hmacShaKeyFor(
            "spring4demo-secret-key-for-jwt-token-generation-and-validation".getBytes(StandardCharsets.UTF_8));
    
    /**
     * 生成访问令牌
     */
    public static String generateAccessToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, SecurityConstants.JWT_ACCESS_TOKEN_EXPIRATION);
    }
    
    /**
     * 生成刷新令牌
     */
    public static String generateRefreshToken(String subject) {
        return generateToken(subject, null, SecurityConstants.JWT_REFRESH_TOKEN_EXPIRATION);
    }
    
    /**
     * 生成令牌
     */
    private static String generateToken(String subject, Map<String, Object> claims, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);
        
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(KEY)
                .compact();
    }
    
    /**
     * 从令牌中获取主题
     */
    public static String getSubjectFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }
    
    /**
     * 从令牌中获取声明
     */
    public static Claims getClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * 验证令牌
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 检查令牌是否过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }
    
    /**
     * 从令牌中提取用户名
     */
    public static String extractUsername(String token) {
        return getSubjectFromToken(token);
    }
    
    /**
     * 从令牌中提取过期时间
     */
    public static Date extractExpiration(String token) {
        return getClaimsFromToken(token).getExpiration();
    }
}