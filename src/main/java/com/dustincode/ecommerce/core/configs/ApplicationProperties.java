package com.dustincode.ecommerce.core.configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final String basePortalUrl;
    private final SecurityProperties security;
    private final CorsConfiguration cors;

    public record SecurityProperties(
            JwtProperties jwt,
            MFAProperties mfa
    ) {}

    public record JwtProperties(
            String secret
    ) {}

    public record MFAProperties(
            String issuer
    ) {}
}
