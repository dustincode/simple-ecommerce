package com.dustincode.ecommerce.core.security;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

public final class SecurityUtils {

    private static final GoogleAuthenticator GOOGLE_AUTHENTICATOR = new GoogleAuthenticator();
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private SecurityUtils() {}

    public static Optional<UserContext> getUserContext() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getCredentials)
                .map(UserContext.class::cast);
    }

    public static Long getUserId() {
        return getUserContext().map(UserContext::userId).orElse(null);
    }

    public static String getAccessTokenId() {
        return getUserContext().map(UserContext::accessTokenId).orElse(null);
    }

    public static String getRefreshTokenId() {
        return getUserContext().map(UserContext::refreshTokenId).orElse(null);
    }

    public static String getEmail() {
        return getUserContext().map(UserContext::email).orElse(null);
    }

    public static String generateRandomCode() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static boolean authorizeMFA(String secret, String code) {
        return NumberUtils.isDigits(code) && GOOGLE_AUTHENTICATOR.authorize(secret, Integer.parseInt(code));
    }

    public static Pair<String, String> generateMFA(String issuer, String email) {
        GoogleAuthenticatorKey credentials = GOOGLE_AUTHENTICATOR.createCredentials();
        String qrUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL(issuer, email, credentials);
        return Pair.of(credentials.getKey(), qrUrl);
    }

    public static BCryptPasswordEncoder getEncoder() {
        return passwordEncoder;
    }

    public static String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public static boolean matchPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
