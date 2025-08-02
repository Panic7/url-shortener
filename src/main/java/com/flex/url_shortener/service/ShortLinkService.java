package com.flex.url_shortener.service;

import com.flex.url_shortener.entity.ShortLink;
import com.flex.url_shortener.event.OriginalLinkAccessed;
import com.flex.url_shortener.repository.ShortLinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShortLinkService {
    private final ShortLinkRepository shortLinkRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final ShortUrlBuilder shortUrlBuilder;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public String shortenUrl(String originalUrl) {
        var nextId = shortLinkRepository.getNextSequenceValue();

        var shortLink = ShortLink.builder()
                .originalUrl(originalUrl)
                .shortCode(shortCodeGenerator.generateShortCode(nextId))
                .build();

        shortLink = shortLinkRepository.save(shortLink);

        return shortUrlBuilder.buildShortUrl(shortLink.getShortCode()).toString();
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String shortCode) {
        var shortLink = shortLinkRepository.findByShortCode(shortCode).orElseThrow();

        eventPublisher.publishEvent(new OriginalLinkAccessed(shortLink));

        return shortLink.getOriginalUrl();
    }
}