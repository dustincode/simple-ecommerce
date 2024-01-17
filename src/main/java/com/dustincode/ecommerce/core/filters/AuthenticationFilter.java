package com.dustincode.ecommerce.core.filters;

import com.dustincode.ecommerce.core.exceptions.AuthenticationException;
import com.dustincode.ecommerce.core.security.UserContext;
import com.dustincode.ecommerce.core.security.jwt.ExtractJwtResult;
import com.dustincode.ecommerce.core.security.jwt.JwtProvider;
import com.dustincode.ecommerce.user.service.AuthService;
import com.dustincode.ecommerce.core.utils.StringUtils;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Set;

import static com.dustincode.ecommerce.core.constant.CommonConstants.REFRESH_TOKEN_URL;
import static com.dustincode.ecommerce.core.constant.HeaderConstants.USER_ACCESS_TOKEN_HEADER;
import static com.dustincode.ecommerce.core.constant.MessageConstant.ACCESS_TOKEN_EXPIRED_ERR;
import static com.dustincode.ecommerce.core.constant.MessageConstant.ACCESS_TOKEN_INVALID_ERR;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final AuthService authService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {

        if (StringUtils.isNotBlank(request.getHeader(USER_ACCESS_TOKEN_HEADER))) {
            handleRequestWithAccessToken(request, response, filterChain);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void handleRequestWithAccessToken(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) {
        try {
            final String accessToken = request.getHeader(USER_ACCESS_TOKEN_HEADER);
            final ExtractJwtResult accessTokenExtracted = jwtProvider.extractClaims(accessToken);

            if (accessTokenExtracted.isInvalid()) {
                throw new AuthenticationException(ACCESS_TOKEN_INVALID_ERR);
            }

            if (accessTokenExtracted.isExpired() && isNotRefreshToken(request)) {
                throw new AuthenticationException(ACCESS_TOKEN_EXPIRED_ERR);
            }

            if (!authService.validateSession(accessTokenExtracted.getTokenId())) {
                throw new AuthenticationException(ACCESS_TOKEN_INVALID_ERR);
            }

            final Set<String> authorities = accessTokenExtracted.getAuthorities();

            UserContext userContext = new UserContext(
                    accessTokenExtracted.getUserId(),
                    accessTokenExtracted.getEmail(),
                    accessTokenExtracted.getTokenId(),
                    accessTokenExtracted.getRefreshTokenId()
            );

            final Authentication authentication = new UsernamePasswordAuthenticationToken(
                    accessTokenExtracted.getUserId(),
                    userContext,
                    authorities.stream().map(SimpleGrantedAuthority::new).toList()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }

    private boolean isNotRefreshToken(final HttpServletRequest request) {
        return !REFRESH_TOKEN_URL.equals(request.getRequestURI());
    }
}
