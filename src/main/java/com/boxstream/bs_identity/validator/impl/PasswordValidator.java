package com.boxstream.bs_identity.validator.impl;

import com.boxstream.bs_identity.validator.PasswordConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordConstraint, String> {

    private int min;

    @Override
    public void initialize(PasswordConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        if (s == null || s.isEmpty()) {
            addConstraintViolation(context, "Password cannot be null or empty");
            return false;
        }

        if (s.length() < min) {
            addConstraintViolation(context, "Password must be at least " + min + " characters long");
            return false;
        }

        if (s.chars().noneMatch(Character::isUpperCase)) {
            addConstraintViolation(context, "Password must contain at least one uppercase letter");
            return false;
        }

        if (s.chars().noneMatch(Character::isLowerCase)) {
            addConstraintViolation(context, "Password must contain at least one lowercase letter");
            return false;
        }

        if (s.chars().noneMatch(Character::isDigit)) {
            addConstraintViolation(context, "Password must contain at least one numeric character");
            return false;
        }

        if (s.chars().noneMatch(ch -> !Character.isLetterOrDigit(ch))) {
            addConstraintViolation(context, "Password must contain at least one special character");
            return false;
        }

        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }


}
