package com.flex.url_shortener.controller;

import com.flex.url_shortener.dto.AuthRequest;
import com.flex.url_shortener.dto.UserResponse;
import com.flex.url_shortener.exception.AuthenticationCookieMissing;
import com.flex.url_shortener.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthenticationController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserResponse> authenticate(
            @Valid @NotNull @RequestBody AuthRequest authRequest) {
        var response = authService.authenticate(authRequest);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.cookieAccessToken().toString())
                .header(HttpHeaders.SET_COOKIE, response.cookieRefreshToken().toString())
                .body(response.userResponse());
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserResponse> refreshToken(
            @CookieValue(value = "${security.jwt.refresh-token.cookie.name}", required = false) Optional<String> refreshToken) {
        var response = authService.tokenRefresh(refreshToken.orElseThrow(AuthenticationCookieMissing::new));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.cookieAccessToken().toString())
                .header(HttpHeaders.SET_COOKIE, response.cookieRefreshToken().toString())
                .body(response.userResponse());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        var response = authService.logout();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.cookieAccessToken().toString())
                .header(HttpHeaders.SET_COOKIE, response.cookieRefreshToken().toString())
                .build();
    }

    @GetMapping("/current-user")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }
}
