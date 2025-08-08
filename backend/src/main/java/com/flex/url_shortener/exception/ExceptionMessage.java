package com.flex.url_shortener.exception;

import lombok.Builder;

import java.util.Date;

@Builder
public record ExceptionMessage(int status, Date date, String description, String url) {
}