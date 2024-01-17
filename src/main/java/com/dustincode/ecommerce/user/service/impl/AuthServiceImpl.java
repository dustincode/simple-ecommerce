package com.dustincode.ecommerce.user.service.impl;

import com.dustincode.ecommerce.core.exceptions.AuthenticationException;
import com.dustincode.ecommerce.core.exceptions.BadRequestException;
import com.dustincode.ecommerce.core.security.jwt.ExtractJwtResult;
import com.dustincode.ecommerce.core.security.jwt.GenerateJwtResult;
import com.dustincode.ecommerce.core.security.jwt.JwtProvider;
import com.dustincode.ecommerce.user.dto.LoginRequest;
import com.dustincode.ecommerce.user.entity.User;
import com.dustincode.ecommerce.user.entity.UserSession;
import com.dustincode.ecommerce.user.repository.UserSessionRepository;
import com.dustincode.ecommerce.user.service.AuthService;
import com.dustincode.ecommerce.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import static com.dustincode.ecommerce.core.constant.MessageConstant.INVALID_CREDENTIAL_ERR;
import static com.dustincode.ecommerce.core.constant.MessageConstant.INVALID_REFRESH_TOKEN_ERR;
import static com.dustincode.ecommerce.core.constant.MessageConstant.USER_NOT_FOUND_ERR;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    /** Repositories */
    private final UserSessionRepository userSessionRepository;

    /** Services */
    private final UserQueryService userQueryService;

    /** Others */
    private final JwtProvider jwtProvider;

    @Override
    public boolean validateSession(String accessTokenId) {
        return userSessionRepository.existsByAccessTokenId(accessTokenId);
    }

    @Override
    @Transactional
    public GenerateJwtResult login(LoginRequest request) {
        return userQueryService
                .getUserByEmailOrPhone(request.getEmail(), request.getPhone())
                .filter(user -> user.validatePassword(request.getPassword()))
                .filter(user -> user.validateMFA(request.getMfaCode()))
                .map(user -> {
                    GenerateJwtResult token = jwtProvider.generateTokens(user);
                    user.addUserSession(
                            token.accessTokenId(),
                            token.refreshTokenId(),
                            token.expireTime()
                    );
                    return token;
                })
                .orElseThrow(() -> new AuthenticationException(INVALID_CREDENTIAL_ERR));
    }

    @Override
    @Transactional
    public GenerateJwtResult refreshAccessToken(String accessToken, String refreshToken) {
        ExtractJwtResult accessTokenExtracted = jwtProvider.extractClaims(accessToken);
        ExtractJwtResult refreshTokenExtracted = jwtProvider.extractClaims(refreshToken);

        String accessTokenId = accessTokenExtracted.getTokenId();
        String refreshTokenId = refreshTokenExtracted.getTokenId();

        if (Objects.isNull(accessTokenId)
                || Objects.isNull(refreshTokenId)
                || refreshTokenExtracted.isExpired()
        ) {
            throw new AuthenticationException(INVALID_REFRESH_TOKEN_ERR);
        }

        return userSessionRepository
                .findByAccessTokenIdAndRefreshTokenId(accessTokenId, refreshTokenId)
                .map(UserSession::getUser)
                .map(user -> {
                    GenerateJwtResult jwtPair = jwtProvider.refreshAccessToken(refreshToken, user);
                    user.removeUserSession(accessTokenId);
                    user.addUserSession(
                            jwtPair.accessTokenId(),
                            jwtPair.refreshTokenId(),
                            jwtPair.expireTime()
                    );
                    return jwtPair;
                })
                .orElseThrow(() -> new AuthenticationException(INVALID_REFRESH_TOKEN_ERR));
    }

    @Override
    @Transactional
    public void logout(Long userId, String accessToken) {
        User user = userQueryService
                .getUserById(userId)
                .orElseThrow(() -> new BadRequestException(USER_NOT_FOUND_ERR));

        Optional.ofNullable(jwtProvider.extractClaims(accessToken))
                .filter(ExtractJwtResult::isNotInvalid)
                .map(ExtractJwtResult::getTokenId)
                .ifPresent(user::removeUserSession);
    }
}
