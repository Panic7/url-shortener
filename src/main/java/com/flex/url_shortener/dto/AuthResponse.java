package com.flex.url_shortener.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import org.springframework.http.ResponseCookie;

@Builder
public record AuthResponse(
        @JsonIgnore ResponseCookie cookieAccessToken,
        @JsonIgnore ResponseCookie cookieRefreshToken,
        UserResponse userResponse
) {
}
