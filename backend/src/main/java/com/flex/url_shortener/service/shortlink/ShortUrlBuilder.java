package com.flex.url_shortener.service.shortlink;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.InvalidUrlException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;

@Service
@Slf4j
public class ShortUrlBuilder {

    @Value("${app.base-url}")
    private String baseUrl;

    public URL buildShortUrl(String shortCode) {
        try {
            return UriComponentsBuilder.fromUriString(baseUrl)
                    .pathSegment(shortCode)
                    .build().toUri().toURL();
        } catch (MalformedURLException e) {
            log.error("Unable to create URL object, baseUrl: {}, shortCode: {}", shortCode, baseUrl, e);
            throw new InvalidUrlException("Unable to create URL object");
        }
    }
}
