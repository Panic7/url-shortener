package com.flex.url_shortener.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

import java.util.Collection;

@Builder
public record PageResponse<T>(
        Collection<T> content,
        Long totalPages,
        Long totalElements,
        Long size,
        Long number) {

    public static <T> PageResponse<T> fromPagedModel(PagedModel<T> pagedModel) {
        return PageResponse.<T>builder()
                .content(pagedModel.getContent())
                .totalPages(pagedModel.getMetadata().totalPages())
                .totalElements(pagedModel.getMetadata().totalElements())
                .size(pagedModel.getMetadata().size())
                .number(pagedModel.getMetadata().number())
                .build();
    }

    public static <T> PageResponse<T> fromPage(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .totalPages((long) page.getTotalPages())
                .totalElements(page.getTotalElements())
                .size((long) page.getSize())
                .number((long) page.getNumber())
                .build();
    }
}
