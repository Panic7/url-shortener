package com.flex.url_shortener.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.flex.url_shortener.entity.Role;
import com.flex.url_shortener.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    private static final String CLAIM_NAME_ROLE = "role";

    @Value("${security.jwt.access-token.validity}")
    private Long accessTokenValidityInMs;

    @Value("${security.jwt.refresh-token.validity}")
    private Long refreshTokenValidityInMs;

    @Value("${security.jwt.access-token.cookie.name}")
    private String accessTokenCookieName;

    private final JwtSignProvider signAlgorithm;
    private final CookieService cookieService;

    public String getAccessTokenFromRequest(HttpServletRequest request) {
        return cookieService.getCookieValueFromRequest(request, accessTokenCookieName);
    }

    public String extractSubject(String token) {
        return decodeJWT(token).getSubject();
    }

    public Instant extractExpiresAt(String token) {
        return decodeJWT(token).getExpiresAtAsInstant();
    }

    public String createAccessToken(String email, Set<Role> roles) {
        var tokenIssuedAt = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        var tokenExpiration = tokenIssuedAt.plusMillis(accessTokenValidityInMs);

        return JWT.create()
                .withSubject(email)
                .withClaim(CLAIM_NAME_ROLE, roles.toString())
                .withIssuedAt(tokenIssuedAt)
                .withExpiresAt(tokenExpiration)
                .sign(signAlgorithm.getSignAlgorithm());
    }

    public String createRefreshToken(String email) {
        var tokenIssuedAt = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        var tokenExpiration = tokenIssuedAt.plusMillis(refreshTokenValidityInMs);

        return JWT.create()
                .withSubject(email)
                .withIssuedAt(tokenIssuedAt)
                .withExpiresAt(tokenExpiration)
                .sign(signAlgorithm.getSignAlgorithm());
    }

    public void validateRefreshToken(String token) {
        JWT.require(signAlgorithm.getSignAlgorithm())
                .build()
                .verify(token);
    }

    public void validateAccessToken(String token, String email) {
        JWT.require(signAlgorithm.getSignAlgorithm())
                .withSubject(email)
                .withClaimPresence(CLAIM_NAME_ROLE)
                .build()
                .verify(token);
    }

    /**
     * Decode JWT token without validation.
     *
     * @param token the JWT token to decode
     * @return DecodedJWT object
     * @throws JWTDecodeException if any part of the token contained an invalid jwt or JSON format of each of the jwt parts
     * @see DecodedJWT
     */
    private DecodedJWT decodeJWT(String token) {
        return JWT.decode(token);
    }


}
