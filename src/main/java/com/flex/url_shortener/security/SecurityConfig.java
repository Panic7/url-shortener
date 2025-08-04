package com.flex.url_shortener.security;

import static com.flex.url_shortener.common.ApplicationConstants.SecurityPaths.H2_CONSOLE;
import static com.flex.url_shortener.common.ApplicationConstants.SecurityPaths.LOGIN;
import static com.flex.url_shortener.common.ApplicationConstants.SecurityPaths.LOGOUT;
import static com.flex.url_shortener.common.ApplicationConstants.SecurityPaths.MY_SHORT_LINKS;
import static com.flex.url_shortener.common.ApplicationConstants.SecurityPaths.SIGN_UP;
import static com.flex.url_shortener.common.ApplicationConstants.SecurityPaths.TOKEN_REFRESH;
import static org.springframework.security.config.Customizer.withDefaults;

import com.flex.url_shortener.entity.Role;
import com.flex.url_shortener.security.jwt.JwtAccessDeniedHandler;
import com.flex.url_shortener.security.jwt.JwtAuthEntryPoint;
import com.flex.url_shortener.security.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        return http.cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(
                        HeadersConfigurer.FrameOptionsConfig::sameOrigin)) // Allow H2 console to be accessed
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(LOGIN, SIGN_UP, LOGOUT, TOKEN_REFRESH, "/*", H2_CONSOLE + "/**").permitAll()
                        .requestMatchers(MY_SHORT_LINKS).hasAuthority(Role.USER.name())
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Cache-Control", "Content-Type"));

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
