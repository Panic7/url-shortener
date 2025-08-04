package com.flex.url_shortener.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flex.url_shortener.common.ApplicationConstants;
import com.flex.url_shortener.exception.ExceptionMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.error("Unauthorized access attempt, access denied: ", accessDeniedException);

        var message = ExceptionMessage.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .date(new Date())
                .description(ApplicationConstants.ExceptionMessage.UNAUTHENTICATED)
                .url(request.getRequestURL().toString())
                .build();

        response.getWriter().println(objectMapper.writeValueAsString(message));
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}
