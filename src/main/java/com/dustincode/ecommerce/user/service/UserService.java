package com.dustincode.ecommerce.user.service;

import com.dustincode.ecommerce.user.dto.ChangePasswordRequest;
import com.dustincode.ecommerce.user.dto.ConfirmResetPasswordRequest;
import com.dustincode.ecommerce.user.dto.RegisterRequest;
import com.dustincode.ecommerce.user.dto.UpdateUserContactInfoRequest;

public interface UserService {

    void createUser(RegisterRequest request);
    void changePassword(Long userId, ChangePasswordRequest request);
    void requestResetPassword(String email);
    void confirmResetPassword(ConfirmResetPasswordRequest request);
    void updateContactInfo(Long userId, UpdateUserContactInfoRequest request);
}
