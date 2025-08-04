package com.flex.url_shortener.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PageableValidator implements ConstraintValidator<MaxPageSize, Pageable> {
    private int maxPerPage;

    @Override
    public void initialize(MaxPageSize constraintAnnotation) {
        maxPerPage = constraintAnnotation.maxPerPage();
    }

    @Override
    public boolean isValid(Pageable pageable, ConstraintValidatorContext context) {
        customMessageForValidation(context);
        return pageable.getPageSize() <= maxPerPage;
    }

    private void customMessageForValidation(ConstraintValidatorContext context) {
        var message = String.format("Page size must not exceed %d.", maxPerPage);
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
