package com.flex.url_shortener.service;

import com.flex.url_shortener.common.ApplicationConstants.CookiePaths;
import com.flex.url_shortener.dto.AuthRequest;
import com.flex.url_shortener.dto.AuthResponse;
import com.flex.url_shortener.dto.UserResponse;
import com.flex.url_shortener.entity.RefreshToken;
import com.flex.url_shortener.mapper.UserMapper;
import com.flex.url_shortener.security.UserDetailsImpl;
import com.flex.url_shortener.security.UserDetailsServiceImpl;
import com.flex.url_shortener.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${security.jwt.access-token.cookie.name}")
    private String accessTokenCookieName;

    @Value("${security.jwt.refresh-token.cookie.name}")
    private String refreshTokenCookieName;

    @Value("${security.jwt.access-token.cookie.validity}")
    private long accessTokenCookieLifetime;

    @Value("${security.jwt.refresh-token.cookie.validity}")
    private long refreshTokenCookieLifetime;

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final CookieService cookieService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;

    @Transactional
    public AuthResponse authenticate(AuthRequest authRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authRequest.email(), authRequest.password()));

        var user = userDetailsService.loadUserByUsername(authRequest.email());

        var accessToken = jwtService.createAccessToken(user.getEmail(), user.getRoles());
        var refreshToken = refreshTokenService.rotate(user.getEmail());

        return AuthResponse.builder()
                .cookieAccessToken(createAccessTokenCookie(accessToken))
                .cookieRefreshToken(createRefreshTokenCookie(refreshToken))
                .userResponse(userMapper.toResponse(user)).build();
    }

    public AuthResponse tokenRefresh(String refreshToken) {
        jwtService.validateRefreshToken(refreshToken);
        var user = userDetailsService.loadUserByUsername(jwtService.extractSubject(refreshToken));
        var accessToken = jwtService.createAccessToken(user.getEmail(), user.getRoles());
        var newRefreshToken = refreshTokenService.rotate(user.getEmail());

        return AuthResponse.builder()
                .userResponse(userMapper.toResponse(user))
                .cookieAccessToken(createAccessTokenCookie(accessToken))
                .cookieRefreshToken(createRefreshTokenCookie(newRefreshToken))
                .build();
    }

    public AuthResponse logout() {
        var accessTokenCookie = cookieService.deleteCookie(accessTokenCookieName, CookiePaths.ACCESS_TOKEN);
        var refreshTokenCookie = cookieService.deleteCookie(refreshTokenCookieName, CookiePaths.REFRESH_TOKEN);

        return AuthResponse.builder()
                .cookieAccessToken(accessTokenCookie)
                .cookieRefreshToken(refreshTokenCookie)
                .build();
    }

    public UserDetailsImpl getUserDetails(boolean throwIfNotAuthenticated) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl authUser)) {
            if (throwIfNotAuthenticated) {
                throw new AuthenticationCredentialsNotFoundException("User is not authenticated.");
            }
            return null;
        }

        return authUser;
    }

    public UserResponse getCurrentUser() {
        var userDetails = getUserDetails(false);
        return userMapper.toResponse(userDetails);
    }

    /**
     * Create a cookie with access token.
     *
     * @param accessToken the access token to include in the cookie
     * @return access token in {@link ResponseCookie}
     */
    private ResponseCookie createAccessTokenCookie(String accessToken) {

        return cookieService.createCookie(accessTokenCookieName,
                accessToken,
                CookiePaths.ACCESS_TOKEN, accessTokenCookieLifetime);
    }

    /**
     * Create a cookie with refresh token.
     *
     * @param refreshToken the {@link RefreshToken} to include in the cookie
     * @return refresh token in {@link ResponseCookie}
     */
    private ResponseCookie createRefreshTokenCookie(RefreshToken refreshToken) {
        return cookieService.createCookie(refreshTokenCookieName,
                refreshToken.getToken(),
                CookiePaths.REFRESH_TOKEN, refreshTokenCookieLifetime);
    }
}
