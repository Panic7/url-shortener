package com.flex.url_shortener.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApplicationConstants {

    @UtilityClass
    public class CookiePaths {
        public static final String REFRESH_TOKEN = "/auth/refresh";
        public static final String ACCESS_TOKEN = "/";
    }

    @UtilityClass
    public class SecurityPaths {
        public static final String SHORTEN = "/shorten";
        public static final String TOKEN_REFRESH = "/auth/refresh";
        public static final String LOGOUT = "/auth/logout";
        public static final String LOGIN = "/auth/login";
        public static final String SIGN_UP = "/users/signup";
        public static final String H2_CONSOLE = "/h2-console";
        public static final String MY_SHORT_LINKS = "/short-links/users/me";
    }

    @UtilityClass
    public class DataValidation {
        public static final int MIN_SIZE_EMAIL = 6;
        public static final int MAX_SIZE_EMAIL = 30;
        public static final int MIN_SIZE_PASSWORD = 4;
        public static final int MAX_SIZE_PASSWORD = 16;
        public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        public static final String SHORT_CODE_REGEX = "^[1-9A-HJ-NP-Za-km-z]{6}$";
        public static final int DEFAULT_MAX_PAGE_SIZE = 20;
    }

    @UtilityClass
    public class ExceptionMessage {
        public static final String UNAUTHENTICATED = "The resource you're trying to reach requires authentication. Please authenticate and try again.";
        public static final String UNAUTHORIZED = "You do not have the necessary permissions to access this resource.";
    }
}
