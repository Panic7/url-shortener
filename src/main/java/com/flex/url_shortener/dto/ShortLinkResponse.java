package com.flex.url_shortener.dto;

public record ShortLinkResponse(String shortUrl,
                                String originalUrl,
                                String shortCode,
                                Long clickCount) {
}
