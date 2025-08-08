package com.flex.url_shortener.validation;

import static com.flex.url_shortener.common.ApplicationConstants.DataValidation.DEFAULT_MAX_PAGE_SIZE;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PageableValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxPageSize {

    String message() default "{validation.constraints.MaxPageSize.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int maxPerPage() default DEFAULT_MAX_PAGE_SIZE;
}
