package com.boxstream.bs_identity.validator;

import com.boxstream.bs_identity.validator.impl.DobValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD}) // where this @validator work? Field: for a properties in a class
@Retention(RetentionPolicy.RUNTIME) // When will handle this @validator custom ?
@Constraint(
        validatedBy = {DobValidator.class} // register the class that will handle this validation
)
public @interface DobConstraint {
    // Three basic properties of a custom validator:
    String message() default "Invalid date of birth";

    int min();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
