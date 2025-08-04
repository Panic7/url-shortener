package com.flex.url_shortener.service;

import com.flex.url_shortener.entity.RefreshToken;
import com.flex.url_shortener.repository.UserRepository;
import com.flex.url_shortener.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional
    public RefreshToken rotate(String userEmail) {
        var user = userRepository.findByEmailWithRefreshToken(userEmail).orElseThrow();
        var token = jwtService.createRefreshToken(userEmail);
        var tokenExpiration = jwtService.extractExpiresAt(token);

        var persistentRefreshToken = Optional.ofNullable(user.getRefreshToken());
        persistentRefreshToken.ifPresentOrElse(
                rt -> {
                    rt.setToken(token);
                    rt.setExpiringDate(tokenExpiration);
                },
                () -> user.setRefreshToken(RefreshToken.builder()
                        .token(token)
                        .expiringDate(tokenExpiration)
                        .build())
        );

        return user.getRefreshToken();
    }

    @Transactional
    public void revoke(String userEmail) {
        userRepository.findByEmailWithRefreshToken(userEmail).ifPresent(u -> u.setRefreshToken(null));
    }

}