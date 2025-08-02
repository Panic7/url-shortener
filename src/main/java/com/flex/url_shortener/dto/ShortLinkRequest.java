package com.flex.url_shortener.dto;

import com.flex.url_shortener.validation.ValidUrl;
import jakarta.validation.constraints.Size;

public record ShortLinkRequest(
        @ValidUrl @Size(max = 13000, message = "{validation.constraints.MaxSizeExceeded.message}") String url) {
}
