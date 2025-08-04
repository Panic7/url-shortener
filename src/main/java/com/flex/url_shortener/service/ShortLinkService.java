package com.flex.url_shortener.service;

import com.flex.url_shortener.dto.PageResponse;
import com.flex.url_shortener.dto.ShortLinkRequest;
import com.flex.url_shortener.dto.ShortLinkResponse;
import com.flex.url_shortener.dto.ShortUrlDto;
import com.flex.url_shortener.entity.ShortLink;
import com.flex.url_shortener.event.OriginalLinkAccessed;
import com.flex.url_shortener.mapper.ShortLinkMapper;
import com.flex.url_shortener.repository.ShortLinkRepository;
import com.flex.url_shortener.service.shortlink.ShortCodeGenerator;
import com.flex.url_shortener.service.shortlink.ShortUrlBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShortLinkService {
    private final ShortLinkRepository shortLinkRepository;
    private final ShortLinkMapper shortLinkMapper;
    private final ShortCodeGenerator shortCodeGenerator;
    private final ShortUrlBuilder shortUrlBuilder;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthService authService;
    private final UserService userService;

    @Transactional
    public ShortUrlDto shortenUrl(ShortLinkRequest shortLinkRequest) {
        var nextId = shortLinkRepository.getNextSequenceValue();
        var shortCode = shortLinkRequest.shortCode() != null
                ? shortLinkRequest.shortCode()
                : shortCodeGenerator.generateShortCode(nextId);
        var userDetails = Optional.ofNullable(authService.getUserDetails(false));
        var user = userDetails.map(ud -> userService.findByEmail(ud.getUsername())).orElse(null);

        var shortLink = ShortLink.builder()
                .originalUrl(shortLinkRequest.url())
                .shortCode(shortCode)
                .user(user)
                .build();

        shortLink = shortLinkRepository.save(shortLink);

        return new ShortUrlDto(shortUrlBuilder.buildShortUrl(shortLink.getShortCode()).toString());
    }

    @Transactional(readOnly = true)
    public URI getOriginalUrl(String shortCode) {
        var shortLink = shortLinkRepository.findByShortCode(shortCode).orElseThrow();

        eventPublisher.publishEvent(new OriginalLinkAccessed(shortLink));

        return URI.create(shortLink.getOriginalUrl());
    }

    @Transactional(readOnly = true)
    public PageResponse<ShortLinkResponse> getMyShortLinks(Pageable pageable) {
        var userEmail = authService.getUserDetails(true).getUsername();
        var shortLinks = shortLinkRepository.findAllByUserEmail(userEmail, pageable);

        return PageResponse.fromPage(shortLinks.map(shortLinkMapper::toResponse));
    }
}