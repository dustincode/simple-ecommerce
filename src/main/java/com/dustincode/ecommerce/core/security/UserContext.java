package com.dustincode.ecommerce.core.security;

public record UserContext(
        Long userId,
        String email,
        String accessTokenId,
        String refreshTokenId
) {
}
