package com.flex.url_shortener.security.jwt;

import static com.flex.url_shortener.common.ApplicationConstants.SecurityPaths.LOGIN;
import static com.flex.url_shortener.common.ApplicationConstants.SecurityPaths.LOGOUT;
import static com.flex.url_shortener.common.ApplicationConstants.SecurityPaths.TOKEN_REFRESH;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {
        var token = jwtService.getAccessTokenFromRequest(request);

        if (token != null && shouldAuthenticate(request)) {
            try {
                var username = jwtService.extractSubject(token);
                var userDetails = userDetailsService.loadUserByUsername(username);
                jwtService.validateAccessToken(token, username);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    var authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (UsernameNotFoundException e) {
                handlerExceptionResolver.resolveException(request, response, null,
                        new JWTVerificationException(e.getMessage()));
                return;
            } catch (Exception e) {
                handlerExceptionResolver.resolveException(request, response, null, e);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Checks if the request should be authenticated based on the URL.
     * Authentication is excessive for logout, token refresh, and login endpoints.
     *
     * @param request the HTTP request to check
     * @return true if authentication should be performed, false otherwise
     */
    private boolean shouldAuthenticate(HttpServletRequest request) {
        var url = request.getRequestURL().toString();
        return !url.endsWith(LOGOUT) && !url.endsWith(TOKEN_REFRESH) && !url.endsWith(LOGIN);
    }

}
