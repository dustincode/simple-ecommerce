package com.dustincode.ecommerce.user.service.impl;

import com.dustincode.ecommerce.user.entity.User;
import com.dustincode.ecommerce.user.repository.UserRepository;
import com.dustincode.ecommerce.user.service.UserQueryService;
import com.dustincode.ecommerce.core.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserByEmailOrPhone(String email, String phone) {
        return StringUtils.isNotBlank(email)
                ? getUserByEmail(email)
                : userRepository.findByPhone(phone);
    }
}
