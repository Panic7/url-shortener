package com.flex.url_shortener.controller;

import com.flex.url_shortener.dto.PageResponse;
import com.flex.url_shortener.dto.ShortLinkRequest;
import com.flex.url_shortener.dto.ShortLinkResponse;
import com.flex.url_shortener.dto.ShortUrlDto;
import com.flex.url_shortener.service.shortlink.ShortLinkService;
import com.flex.url_shortener.validation.MaxPageSize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class ShortLinkController {
    private final ShortLinkService shortLinkService;

    @PostMapping("/shorten")
    public ResponseEntity<ShortUrlDto> shortenUrl(@Valid @RequestBody ShortLinkRequest request) {
        var shortUrl = shortLinkService.shortenUrl(request);
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable @NotBlank String shortCode) {
        var originalUrl = shortLinkService.getOriginalUrl(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(originalUrl)
                .build();
    }

    @GetMapping("/short-links")
    public ResponseEntity<PageResponse<ShortLinkResponse>> getMyShortLinks(@NotNull @MaxPageSize(maxPerPage = 30) Pageable pageable) {
        var userShortLinks = shortLinkService.getMyShortLinks(pageable);
        return ResponseEntity.ok(userShortLinks);
    }
}