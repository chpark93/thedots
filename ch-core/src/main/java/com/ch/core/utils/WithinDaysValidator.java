package com.ch.core.utils;

import com.ch.core.annotation.WithinDays;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class WithinDaysValidator implements ConstraintValidator<WithinDays, LocalDate> {

    private int plusDays;

    @Override
    public void initialize(WithinDays constraintAnnotation) {
        this.plusDays = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if ( value == null ) {
            return true;
        }

        LocalDate today = LocalDate.now();
        LocalDate daysLater = today.plusDays(this.plusDays);

        // 값이 오늘 이후 -> plusDays 이내인지 검증
        if ( !value.isBefore(today.plusDays(1)) && !value.isAfter(daysLater) ) {
            return true;
        } else {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "reservationDate Must Be Within Next " + this.plusDays + " Days From Today")
                    .addConstraintViolation();

            return false;
        }

    }
}
