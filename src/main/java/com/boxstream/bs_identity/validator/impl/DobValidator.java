package com.boxstream.bs_identity.validator.impl;

import com.boxstream.bs_identity.validator.DobConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate> {

    private int min;

    @Override
    public void initialize(DobConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min(); // get value min that we config in the DTO or Entity
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) return true;
        long years = ChronoUnit.YEARS.between(value, LocalDate.now());
        if (years < min) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Age must be at least " + min + " years")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

}
