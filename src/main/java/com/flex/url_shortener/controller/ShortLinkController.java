package com.flex.url_shortener.controller;

import com.flex.url_shortener.dto.ShortLinkRequest;
import com.flex.url_shortener.dto.ShortLinkResponse;
import com.flex.url_shortener.mapper.ShortLinkMapper;
import com.flex.url_shortener.service.ShortLinkService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
public class ShortLinkController {
    private final ShortLinkService shortLinkService;
    private final ShortLinkMapper shortLinkMapper;

    @PostMapping("/shorten")
    public ResponseEntity<ShortLinkResponse> shortenUrl(@Valid @RequestBody ShortLinkRequest request) {
        var shortUrl = shortLinkService.shortenUrl(request.url());
        return ResponseEntity.ok(shortLinkMapper.toResponse(shortUrl));
    }

    @GetMapping("/{shortCode}")
    public RedirectView redirect(@PathVariable @NotBlank String shortCode) {
        var originalUrl = shortLinkService.getOriginalUrl(shortCode);

        return new RedirectView(originalUrl);
    }
}