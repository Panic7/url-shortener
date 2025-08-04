package com.flex.url_shortener.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.flex.url_shortener.dto.ShortLinkResponse;
import com.flex.url_shortener.entity.ShortLink;
import com.flex.url_shortener.service.shortlink.ShortUrlBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = SPRING, injectionStrategy = CONSTRUCTOR)
public abstract class ShortLinkMapper {

    @Autowired
    private ShortUrlBuilder shortUrlBuilder;

    public abstract ShortLinkResponse toResponse(String shortenedUrl);

    @Mapping(target = "shortUrl", source = "shortCode", qualifiedByName = "shortCodeToShortUrl")
    public abstract ShortLinkResponse toResponse(ShortLink shortLink);

    @Named("shortCodeToShortUrl")
    String shortCodeToShortUrl(String shortCode) {
        return shortUrlBuilder.buildShortUrl(shortCode).toString();
    }

}