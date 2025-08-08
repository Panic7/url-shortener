package com.flex.url_shortener.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.validator.routines.UrlValidator;

public class UrlConstraintValidator implements ConstraintValidator<ValidUrl, String> {
    private final UrlValidator urlValidator = new UrlValidator();

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        return urlValidator.isValid(url);
    }

}
