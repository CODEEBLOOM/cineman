package com.codebloom.cineman.common.utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AfterNowValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterNow {
    String message() default "Thời điểm phải sau thời điểm hiện tại!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}