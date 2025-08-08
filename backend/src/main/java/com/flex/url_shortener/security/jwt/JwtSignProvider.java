package com.flex.url_shortener.security.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class JwtSignProvider {
    private final Algorithm signAlgorithm;

    public JwtSignProvider(@Value("${security.jwt.secret}") String jwtSecretKey) {
        signAlgorithm = Algorithm.HMAC256(jwtSecretKey);
    }
}
