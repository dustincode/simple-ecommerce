package com.dustincode.ecommerce.user.service;

import com.dustincode.ecommerce.user.entity.User;

import java.util.Optional;

public interface UserQueryService {

    Optional<User> getUserById(Long userId);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByEmailOrPhone(String email, String phone);
}
