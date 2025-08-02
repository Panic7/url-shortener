package com.flex.url_shortener.event;

import com.flex.url_shortener.entity.ShortLink;

public record OriginalLinkAccessed(ShortLink shortLink) {
}
