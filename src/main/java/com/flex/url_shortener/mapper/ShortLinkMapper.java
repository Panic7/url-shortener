package com.flex.url_shortener.mapper;

import com.flex.url_shortener.dto.ShortLinkResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ShortLinkMapper {

    ShortLinkResponse toResponse(String shortenedUrl);

}