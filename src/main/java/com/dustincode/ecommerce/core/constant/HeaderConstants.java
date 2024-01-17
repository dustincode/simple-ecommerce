package com.dustincode.ecommerce.core.constant;

public final class HeaderConstants {

    private HeaderConstants() {}

    public static final String USER_ACCESS_TOKEN_HEADER = "x-user-access-token";
    public static final String USER_REFRESH_TOKEN_HEADER = "x-user-refresh-token";
    public static final String IS_TOKEN_EXPIRED_HEADER = "X-Is-Token-Expired";
    public static final String X_TOTAL_COUNT_HEADER = "X-Total-Count";
    public static final String LINK_FORMAT_HEADER = "<{0}>; rel=\"{1}\"";
    public static final String LINK_HEADER = "Link";
    public static final String ACCESS_CONTROL_EXPOSE_HEADERS_HEADER = "Access-Control-Expose-Headers";
}
