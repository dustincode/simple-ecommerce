package com.dustincode.ecommerce.core.constant;

public final class MessageConstant {

    private MessageConstant() {}

    public static final String INVALID_CREDENTIAL_ERR = "error.validate.login.invalid-credential";
    public static final String INVALID_CREDENTIAL_AND_BLOCKED_ERR = "error.validate.login.invalid-email-password-and-account-was-blocked";
    public static final String USER_ALREADY_EXIST_ERR = "error.validate.user.already-exist";
    public static final String USER_NOT_FOUND_ERR = "error.validate.user.not-found";
    public static final String INVALID_USER_PASSWORD_ERR = "error.validate.user.password.invalid";
    public static final String USER_WAS_BLOCKED_ERR = "error.validate.user.blocked";
    public static final String USER_ALREADY_SETUP_MFA_ERR = "error.validate.user.mfa.already-setup";
    public static final String USER_NOT_SETUP_MFA_ERR = "error.validate.user.mfa.not-setup";
    public static final String INVALID_MFA_ERR = "error.validate.user.mfa.invalid";

    public static final String ACCESS_TOKEN_EXPIRED_ERR = "error.validate.access-token.expired";
    public static final String ACCESS_TOKEN_INVALID_ERR = "error.validate.access-token.invalid";
    public static final String INVALID_REFRESH_TOKEN_ERR = "error.validate.refresh-token.invalid";
    public static final String SHOP_ALREADY_DELETED_ERR = "error.validate.shop.already-deleted";
    public static final String COUNTRY_NOT_EXIST_ERR = "error.validate.location.country.not-exist";
    public static final String STATE_NOT_EXIST_ERR = "error.validate.location.state.not-exist";
    public static final String CITY_NOT_EXIST_ERR = "error.validate.location.city.not-exist";
    public static final String CATEGORY_NOT_EXIST_ERR = "error.validate.category.not-exist";
    public static final String TAG_NOT_EXIST_ERR = "error.validate.tag.not-exist";

    public static final String PHONE_ALREADY_EXIST_ERR = "error.validate.user.phone.already-exist";

    public static final String INVALID_RESET_PASSWORD_TOKEN_ERR = "error.validate.reset-password.token.invalid";
}
