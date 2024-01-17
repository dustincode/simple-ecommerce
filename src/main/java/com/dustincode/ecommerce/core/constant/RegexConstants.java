package com.dustincode.ecommerce.core.constant;

public final class RegexConstants {

    private RegexConstants() {}

    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,50}$";
    public static final String PHONE_REGEX = "^\\+?[1-9][0-9]{7,14}$";
}
