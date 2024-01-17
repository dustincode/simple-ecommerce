package com.dustincode.ecommerce.core.utils;

public final class StringUtils {

    private StringUtils() {}

    public static boolean isBlank(String str) {
        return org.apache.commons.lang3.StringUtils.isBlank(str);
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

}
