package com.dustincode.ecommerce.core.security.jwt;

import io.jsonwebtoken.Claims;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record ExtractJwtResult(CheckJwtResult status, Claims claims) {

    public Set<String> getAuthorities() {
        if (status != CheckJwtResult.INVALID && claims.get("authorities") instanceof Collection<?> authorities) {
            return authorities.stream().map(String::valueOf).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    public Long getUserId() {
        return isInvalid() ? null : claims.get("userId", Long.class);
    }

    public String getEmail() {
        return isInvalid() ? null : claims.getSubject();
    }

    public String getRefreshTokenId() {
        return isInvalid() ? null : claims.get("refreshTokenId", String.class);
    }

    public boolean hasMFA() {
        if (status != CheckJwtResult.INVALID) {
            return Optional
                    .ofNullable(claims.get("mfa", Boolean.class))
                    .orElse(Boolean.FALSE);
        }
        return false;
    }

    public String getTokenId() {
        return isInvalid() ? null : claims.getId();
    }

    public boolean isExpired() {
        return CheckJwtResult.EXPIRED == status;
    }

    public boolean isInvalid() {
        return CheckJwtResult.INVALID == status;
    }

    public boolean isNotInvalid() {
        return !isInvalid();
    }
}
