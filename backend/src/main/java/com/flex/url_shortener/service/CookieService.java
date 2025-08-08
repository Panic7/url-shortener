package com.flex.url_shortener.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

@Service
@Slf4j
public class CookieService {

    /**
     * Retrieves the value of a cookie from the request.
     *
     * @param request the HTTP servlet request
     * @param name    the name of the cookie to retrieve
     * @return the value of the cookie, or null if not found
     */
    public String getCookieValueFromRequest(HttpServletRequest request,
                                            String name) {
        var cookie = WebUtils.getCookie(request, name);

        if (cookie == null || cookie.getValue().isBlank()) {
            return null;
        }

        return cookie.getValue();
    }

    /**
     * Creates a cookie with the specified parameters.
     *
     * @param name              the name of the cookie
     * @param value             the value of the cookie
     * @param path              the path for which the cookie is valid
     * @param lifetimeInSeconds the lifetime of the cookie in seconds
     * @return a ResponseCookie object representing the created cookie
     */
    public ResponseCookie createCookie(String name,
                                       String value,
                                       String path,
                                       Long lifetimeInSeconds) {
        return ResponseCookie
                .from(name, value)
                .path(path)
                .maxAge(lifetimeInSeconds)
                .httpOnly(true)
                .secure(true)
                .sameSite(Cookie.SameSite.STRICT.name())
                .build();
    }

    public ResponseCookie deleteCookie(String name,
                                       String path) {
        return createCookie(name, "", path, 0L);
    }
}
