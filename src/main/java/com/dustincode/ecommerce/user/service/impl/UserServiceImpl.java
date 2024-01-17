package com.dustincode.ecommerce.user.service.impl;

import com.dustincode.ecommerce.core.exceptions.BadRequestException;
import com.dustincode.ecommerce.user.dto.ChangePasswordRequest;
import com.dustincode.ecommerce.user.dto.ConfirmResetPasswordRequest;
import com.dustincode.ecommerce.user.dto.RegisterRequest;
import com.dustincode.ecommerce.user.dto.UpdateUserContactInfoRequest;
import com.dustincode.ecommerce.user.entity.User;
import com.dustincode.ecommerce.user.entity.UserToken;
import com.dustincode.ecommerce.user.entity.enumerations.Role;
import com.dustincode.ecommerce.user.repository.UserRepository;
import com.dustincode.ecommerce.user.repository.UserTokenRepository;
import com.dustincode.ecommerce.user.service.UserQueryService;
import com.dustincode.ecommerce.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.dustincode.ecommerce.core.constant.MessageConstant.INVALID_RESET_PASSWORD_TOKEN_ERR;
import static com.dustincode.ecommerce.core.constant.MessageConstant.INVALID_USER_PASSWORD_ERR;
import static com.dustincode.ecommerce.core.constant.MessageConstant.PHONE_ALREADY_EXIST_ERR;
import static com.dustincode.ecommerce.core.constant.MessageConstant.USER_ALREADY_EXIST_ERR;
import static com.dustincode.ecommerce.core.constant.MessageConstant.USER_NOT_FOUND_ERR;
import static com.dustincode.ecommerce.user.entity.enumerations.TokenChannel.EMAIL;
import static com.dustincode.ecommerce.user.entity.enumerations.TokenType.RESET_PASSWORD;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    /** Repositories */
    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;

    /** Services */
    private final UserQueryService userQueryService;

    @Override
    public void createUser(RegisterRequest request) {
        log.info("Request to create new user with email '{}'", request.getEmail());

        if (userRepository.existsByEmailOrPhone(request.getEmail(), request.getPhone())) {
            throw new BadRequestException(USER_ALREADY_EXIST_ERR);
        }

        User user = userRepository.save(new User(
                Role.USER,
                request.getEmail(),
                request.getPhone(),
                request.getPassword(),
                request.getName(),
                request.getAddress()
        ));

        log.info("Created new user with id '{}'", user.getId());
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        log.info("Request to change password for user id '{}'", userId);

        User user = userQueryService
                .getUserById(userId)
                .orElseThrow(() -> new BadRequestException(USER_NOT_FOUND_ERR));

        if (user.updatePassword(request.getOldPassword(), request.getNewPassword())) {
            log.info("Done change password for user id '{}'", userId);
        }

        throw new BadRequestException(INVALID_USER_PASSWORD_ERR);
    }

    @Override
    public void requestResetPassword(String email) {
        userQueryService
                .getUserByEmail(email)
                .ifPresentOrElse(
                        User::createResetPasswordToken,
                        () -> { throw new BadRequestException(USER_NOT_FOUND_ERR); }
                );
    }

    @Override
    public void confirmResetPassword(ConfirmResetPasswordRequest request) {
        userTokenRepository
                .findByTokenAndTypeAndChannel(request.getToken(), RESET_PASSWORD, EMAIL)
                .map(UserToken::getUser)
                .ifPresentOrElse(
                        user -> user.resetPassword(request.getNewPassword()),
                        () -> { throw new BadRequestException(INVALID_RESET_PASSWORD_TOKEN_ERR); }
                );
    }

    @Override
    public void updateContactInfo(Long userId, UpdateUserContactInfoRequest request) {
        log.info("Request to update user's contact info for user id '{}'", userId);

        User user = userQueryService
                .getUserById(userId)
                .orElseThrow(() -> new BadRequestException(USER_NOT_FOUND_ERR));

        boolean isUpdatePhone = !user.getPhone().equals(request.getPhone());

        if (isUpdatePhone && userRepository.existsByPhoneAndIdIsNot(request.getPhone(), userId)) {
            throw new BadRequestException(PHONE_ALREADY_EXIST_ERR);
        }

        user.updateContactInfo(request.getPhone(), request.getName(), request.getAddress());

        log.info("Done update user's contact info for user id '{}'", userId);
    }

}
