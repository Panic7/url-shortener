package com.flex.url_shortener.security.jwt;

import static com.flex.url_shortener.common.ApplicationConstants.ExceptionMessage.UNAUTHENTICATED;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flex.url_shortener.exception.ExceptionMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.error("unauthorized access attempt: ", authException);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        final var message = ExceptionMessage.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .date(new Date())
                .description(UNAUTHENTICATED)
                .url(request.getRequestURL().toString())
                .build();

        response.getWriter().println(objectMapper.writeValueAsString(message));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
