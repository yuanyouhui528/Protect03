package com.leadexchange.common.utils;

import io.jsonwebtoken.*;
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

/**
 * JWT工具类
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    /**
     * JWT密钥
     */
    @Value("${app.jwt.secret}")
    private String secret;

    /**
     * JWT过期时间（毫秒）
     */
    @Value("${app.jwt.expiration}")
    private Long expiration;

    /**
     * JWT请求头
     */
    @Value("${app.jwt.header}")
    private String header;

    /**
     * JWT前缀
     */
    @Value("${app.jwt.prefix}")
    private String prefix;

    /**
     * 用户ID声明键
     */
    private static final String CLAIM_KEY_USER_ID = "userId";

    /**
     * 用户名声明键
     */
    private static final String CLAIM_KEY_USERNAME = "username";

    /**
     * 角色声明键
     */
    private static final String CLAIM_KEY_ROLES = "roles";

    /**
     * 权限声明键
     */
    private static final String CLAIM_KEY_AUTHORITIES = "authorities";

    /**
     * 生成JWT令牌
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param roles 角色列表
     * @param authorities 权限列表
     * @return JWT令牌
     */
    public String generateToken(Long userId, String username, String roles, String authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER_ID, userId);
        claims.put(CLAIM_KEY_USERNAME, username);
        claims.put(CLAIM_KEY_ROLES, roles);
        claims.put(CLAIM_KEY_AUTHORITIES, authorities);
        return generateToken(claims, username);
    }

    /**
     * 生成JWT令牌
     * 
     * @param claims 声明
     * @param subject 主题
     * @return JWT令牌
     */
    public String generateToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从令牌中获取用户名
     * 
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从令牌中获取用户ID
     * 
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object userId = claims.get(CLAIM_KEY_USER_ID);
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        } else if (userId instanceof Long) {
            return (Long) userId;
        }
        return null;
    }

    /**
     * 从令牌中获取角色
     * 
     * @param token JWT令牌
     * @return 角色列表
     */
    public String getRolesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return (String) claims.get(CLAIM_KEY_ROLES);
    }

    /**
     * 从令牌中获取权限
     * 
     * @param token JWT令牌
     * @return 权限列表
     */
    public String getAuthoritiesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return (String) claims.get(CLAIM_KEY_AUTHORITIES);
    }

    /**
     * 从令牌中获取过期时间
     * 
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从令牌中获取签发时间
     * 
     * @param token JWT令牌
     * @return 签发时间
     */
    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    /**
     * 从令牌中获取指定声明
     * 
     * @param token JWT令牌
     * @param claimsResolver 声明解析器
     * @param <T> 声明类型
     * @return 声明值
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从令牌中获取所有声明
     * 
     * @param token JWT令牌
     * @return 声明
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("解析JWT令牌失败: {}", e.getMessage());
            throw new JwtException("无效的JWT令牌", e);
        }
    }

    /**
     * 验证令牌是否过期
     * 
     * @param token JWT令牌
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            logger.warn("检查JWT令牌过期状态失败: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 验证令牌
     * 
     * @param token JWT令牌
     * @param username 用户名
     * @return 是否有效
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = getUsernameFromToken(token);
            return (username.equals(tokenUsername) && !isTokenExpired(token));
        } catch (Exception e) {
            logger.warn("验证JWT令牌失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证令牌格式
     * 
     * @param token JWT令牌
     * @return 是否有效
     */
    public Boolean validateTokenFormat(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("JWT令牌格式无效: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 刷新令牌
     * 
     * @param token JWT令牌
     * @return 新的JWT令牌
     */
    public String refreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            claims.setIssuedAt(new Date());
            claims.setExpiration(new Date(System.currentTimeMillis() + expiration));
            return generateToken(claims, claims.getSubject());
        } catch (Exception e) {
            logger.error("刷新JWT令牌失败: {}", e.getMessage());
            throw new JwtException("刷新令牌失败", e);
        }
    }

    /**
     * 从请求头中提取令牌
     * 
     * @param authHeader 授权头
     * @return JWT令牌
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith(prefix)) {
            return authHeader.substring(prefix.length());
        }
        return null;
    }

    /**
     * 获取签名密钥
     * 
     * @return 签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 获取令牌剩余有效时间（秒）
     * 
     * @param token JWT令牌
     * @return 剩余有效时间
     */
    public Long getTokenRemainingTime(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return Math.max(0, remaining / 1000);
        } catch (Exception e) {
            logger.warn("获取JWT令牌剩余时间失败: {}", e.getMessage());
            return 0L;
        }
    }

    // Getter methods for configuration
    public String getHeader() {
        return header;
    }

    public String getPrefix() {
        return prefix;
    }

    public Long getExpiration() {
        return expiration;
    }
}