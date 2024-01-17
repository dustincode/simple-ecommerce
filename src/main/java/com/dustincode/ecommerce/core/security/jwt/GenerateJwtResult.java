package com.dustincode.ecommerce.core.security.jwt;

import java.time.Instant;

public record GenerateJwtResult(
        String accessTokenId,
        String refreshTokenId,
        String accessToken,
        String refreshToken,
        Instant expireTime
) {}
