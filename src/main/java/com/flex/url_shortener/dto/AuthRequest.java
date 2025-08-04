package com.flex.url_shortener.dto;

import static com.flex.url_shortener.common.ApplicationConstants.DataValidation.MAX_SIZE_EMAIL;
import static com.flex.url_shortener.common.ApplicationConstants.DataValidation.MAX_SIZE_PASSWORD;
import static com.flex.url_shortener.common.ApplicationConstants.DataValidation.MIN_SIZE_EMAIL;
import static com.flex.url_shortener.common.ApplicationConstants.DataValidation.MIN_SIZE_PASSWORD;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank @Size(min = MIN_SIZE_EMAIL, max = MAX_SIZE_EMAIL)
        @Email(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$") String email,
        @NotBlank @Size(min = MIN_SIZE_PASSWORD, max = MAX_SIZE_PASSWORD) String password
) {

}
