package com.dustincode.ecommerce.user.controller;

import com.dustincode.ecommerce.user.dto.ConfirmResetPasswordRequest;
import com.dustincode.ecommerce.user.dto.LoginRequest;
import com.dustincode.ecommerce.user.dto.RegisterRequest;
import com.dustincode.ecommerce.user.dto.ResetPasswordRequest;
import com.dustincode.ecommerce.user.service.AuthService;
import com.dustincode.ecommerce.user.service.UserService;
import com.dustincode.ecommerce.core.security.jwt.GenerateJwtResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static com.dustincode.ecommerce.core.constant.CommonConstants.REFRESH_TOKEN_URL;
import static com.dustincode.ecommerce.core.constant.HeaderConstants.USER_ACCESS_TOKEN_HEADER;
import static com.dustincode.ecommerce.core.constant.HeaderConstants.USER_REFRESH_TOKEN_HEADER;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping(value = "/api/v1/register")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody RegisterRequest request) {
        userService.createUser(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/api/v1/login")
    public ResponseEntity<GenerateJwtResult> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = REFRESH_TOKEN_URL)
    public ResponseEntity<GenerateJwtResult> refreshAccessToken(
            @RequestHeader(USER_ACCESS_TOKEN_HEADER) String accessToken,
            @RequestHeader(USER_REFRESH_TOKEN_HEADER) String refreshToken
    ) {
        return ResponseEntity.ok(authService.refreshAccessToken(accessToken, refreshToken));
    }

    @PostMapping(value = "/api/v1/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.requestResetPassword(request.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/api/v1/reset-password/confirm")
    public ResponseEntity<Void> confirmResetPassword(@Valid @RequestBody ConfirmResetPasswordRequest request) {
        userService.confirmResetPassword(request);
        return ResponseEntity.noContent().build();
    }
}
