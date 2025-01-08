package com.boxstream.bs_identity.validator;

import com.boxstream.bs_identity.validator.impl.DobValidator;
import com.boxstream.bs_identity.validator.impl.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD}) // where this @validator work? Field: for a properties in a class
@Retention(RetentionPolicy.RUNTIME) // When will handle this @validator custom ?
@Constraint(
        validatedBy = {PasswordValidator.class} // register the class that will handle this validation
)
public @interface PasswordConstraint {

    String message() default "Invalid password";

    int min();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
