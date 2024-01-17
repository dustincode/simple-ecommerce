package com.dustincode.ecommerce.user.controller;

import com.dustincode.ecommerce.user.dto.ChangePasswordRequest;
import com.dustincode.ecommerce.user.dto.UpdateUserContactInfoRequest;
import com.dustincode.ecommerce.user.service.AuthService;
import com.dustincode.ecommerce.user.service.UserService;
import com.dustincode.ecommerce.core.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.dustincode.ecommerce.core.constant.HeaderConstants.USER_ACCESS_TOKEN_HEADER;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(USER_ACCESS_TOKEN_HEADER) String accessToken) {
        authService.logout(SecurityUtils.getUserId(), accessToken);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(SecurityUtils.getUserId(), request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/contact-info")
    public ResponseEntity<Void> updateContactInfo(@Valid @RequestBody UpdateUserContactInfoRequest request) {
        userService.updateContactInfo(SecurityUtils.getUserId(), request);
        return ResponseEntity.noContent().build();
    }

}
