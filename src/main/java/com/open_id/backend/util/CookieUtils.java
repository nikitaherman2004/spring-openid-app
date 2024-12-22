package com.open_id.backend.util;

import jakarta.servlet.http.Cookie;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CookieUtils {

    public static Cookie createCookie(String name, String token, int maxAge) {
        Cookie cookie = new Cookie(name, token);

        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);

        return cookie;
    }
}
