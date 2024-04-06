package com.ch.core.annotation;

import com.ch.core.utils.WithinDaysValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WithinDaysValidator.class)
public @interface WithinDays {

    int value();

    String message() default "Invalid reservationDate Field";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
