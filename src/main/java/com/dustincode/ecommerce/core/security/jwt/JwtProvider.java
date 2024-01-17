package com.dustincode.ecommerce.core.security.jwt;

import com.dustincode.ecommerce.core.security.SecurityUtils;
import com.dustincode.ecommerce.user.entity.User;
import com.dustincode.ecommerce.user.entity.enumerations.MFAType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class JwtProvider {
    private static final long ACCESS_TOKEN_IN_MINUTES = 60;
    private static final long REFRESH_TOKEN_IN_HOURS = 24;

    private final Key signingKey;

    public JwtProvider(@Value("${application.security.jwt.secret}") String secret) {
        this.signingKey = getSignInKey(secret);
    }

    public GenerateJwtResult generateTokens(User user) {
        return generateTokens(new HashMap<>(), user);
    }

    public GenerateJwtResult generateTokens(Map<String, Object> extraClaims, User user) {
        String refreshTokenId = SecurityUtils.generateRandomCode();
        String accessTokenId = SecurityUtils.generateRandomCode();
        Date issueAt = new Date(System.currentTimeMillis());

        boolean hasMFA = MFAType.GOOGLE == user.getMfaType();
        Set<String> authorities = Set.of(user.getRole().toString());

        String refreshToken = Jwts.builder()
                .setId(refreshTokenId)
                .claim("userId", user.getId())
                .setSubject(user.getEmail())
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .setIssuedAt(issueAt)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * REFRESH_TOKEN_IN_HOURS))
                .compact();

        Date expireTime = new Date(System.currentTimeMillis() + 1000 * 60 * ACCESS_TOKEN_IN_MINUTES);

        String accessToken = Jwts.builder()
                .setClaims(extraClaims)
                .setId(accessTokenId)
                .claim("userId", user.getId())
                .setSubject(user.getEmail())
                .claim("refreshId", refreshTokenId)
                .claim("authorities", authorities)
                .claim("mfa", hasMFA)
                .setIssuedAt(issueAt)
                .setExpiration(expireTime)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();

        return new GenerateJwtResult(
                accessTokenId,
                refreshTokenId,
                accessToken,
                refreshToken,
                expireTime.toInstant()
        );
    }

    public GenerateJwtResult refreshAccessToken(String refreshToken, User user) {
        String accessTokenId = SecurityUtils.generateRandomCode();
        Date issueAt = new Date(System.currentTimeMillis());

        boolean hasMFA = MFAType.GOOGLE == user.getMfaType();
        Set<String> authorities = Set.of(user.getRole().toString());

        ExtractJwtResult refreshTokenExtracted = extractClaims(refreshToken);
        String refreshTokenId = refreshTokenExtracted.getTokenId();

        Date expireTime = new Date(System.currentTimeMillis() + 1000 * 60 * ACCESS_TOKEN_IN_MINUTES);

        String accessToken = Jwts.builder()
                .setId(accessTokenId)
                .claim("userId", user.getId())
                .setSubject(user.getEmail())
                .claim("refreshId", refreshTokenId)
                .claim("authorities", authorities)
                .claim("mfa", hasMFA)
                .setIssuedAt(issueAt)
                .setExpiration(expireTime)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();

        return new GenerateJwtResult(
                accessTokenId,
                refreshTokenId,
                accessToken,
                refreshToken,
                expireTime.toInstant()
        );
    }

    public ExtractJwtResult extractClaims(String token) {
        try {
            return new ExtractJwtResult(CheckJwtResult.VALID, extractAllClaims(token));
        } catch (ExpiredJwtException exception) {
            return new ExtractJwtResult(CheckJwtResult.EXPIRED, exception.getClaims());
        } catch (Exception exception) {
            return new ExtractJwtResult(CheckJwtResult.INVALID, null);
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey(String secret) {
        final byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
