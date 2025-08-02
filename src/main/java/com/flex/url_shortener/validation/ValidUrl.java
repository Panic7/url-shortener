package com.flex.url_shortener.validation;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Constraint(validatedBy = UrlConstraintValidator.class)
@Target({FIELD, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
public @interface ValidUrl {
    String message() default "{validation.constraints.InvalidUrl.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
