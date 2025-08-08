package com.flex.url_shortener.dto;

import static com.flex.url_shortener.common.ApplicationConstants.DataValidation.SHORT_CODE_REGEX;

import com.flex.url_shortener.validation.ValidUrl;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.lang.Nullable;

@Builder
public record ShortLinkRequest(
        @ValidUrl @Size(max = 13000, message = "{validation.constraints.MaxSizeExceeded.message}")
        String url,
        @Nullable @Pattern(regexp = SHORT_CODE_REGEX, message = "{validation.constraints.ShortCodeInvalid.message}")
        String shortCode) {
}
