package com.dustincode.ecommerce.user.service;

import com.dustincode.ecommerce.user.dto.LoginRequest;
import com.dustincode.ecommerce.core.security.jwt.GenerateJwtResult;

public interface AuthService {
    boolean validateSession(String accessTokenId);
    GenerateJwtResult login(LoginRequest request);
    GenerateJwtResult refreshAccessToken(String accessToken, String refreshToken);
    void logout(Long userId, String accessToken);
}
