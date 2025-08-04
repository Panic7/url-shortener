package com.flex.url_shortener.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
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

    @Value("${security.jwt.access-validity}")
    private Long accessTokenValidityInMs;

    @Value("${security.jwt.refresh-validity}")
    private Long refreshTokenValidityInMs;

    @Value("${security.jwt.access-cookie-name}")
    private String accessTokenCookieName;

    private final JwtSignProvider signAlgorithm;
    private final CookieService cookieService;

    public String getAccessTokenFromRequest(HttpServletRequest request) {
        return cookieService.getCookieValueFromRequest(request, accessTokenCookieName);
    }

    public String extractSubject(String token) {
        return extractDecodedJWT(token).getSubject();
    }

    public Instant extractExpiresAt(String token) {
        return extractDecodedJWT(token).getExpiresAtAsInstant();
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

    public boolean validateRefreshToken(String token, String subject) {
        var decodedJWT = JWT.require(signAlgorithm.getSignAlgorithm())
                .withSubject(subject)
                .build()
                .verify(token);

        var tokenValidityInMs = decodedJWT.getExpiresAtAsInstant().toEpochMilli() -
                decodedJWT.getIssuedAtAsInstant().toEpochMilli();

        return tokenValidityInMs == refreshTokenValidityInMs;
    }

    public boolean validateAccessToken(String token, String email) {
        var decodedJWT = JWT.require(signAlgorithm.getSignAlgorithm())
                .withClaimPresence(CLAIM_NAME_ROLE)
                .build()
                .verify(token);

        var tokenValidityInMs = decodedJWT.getExpiresAtAsInstant().toEpochMilli() -
                decodedJWT.getIssuedAtAsInstant().toEpochMilli();

        return tokenValidityInMs == accessTokenValidityInMs;
    }

    /**
     * Create DecodedJWT object from token.
     * If the token is valid, it returns a DecodedJWT object
     * that can be used to extract certain fields from the token
     * otherwise it throws a JWTVerificationException.
     *
     * @param token the JWT token we will create DecodedJWT from
     * @return DecodedJWT object
     * @see JWTVerificationException
     * @see DecodedJWT
     */
    private DecodedJWT extractDecodedJWT(String token) {

        return JWT.require(signAlgorithm.getSignAlgorithm())
                .build()
                .verify(token);
    }


}
