package com.codebloom.cineman.common.utils;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ShowTimeValidator.class) // Liên kết với lớp validator
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE}) // Áp dụng cho trường hoặc tham số
@Retention(RetentionPolicy.RUNTIME) // Có sẵn trong runtime
public @interface ShowTimeValidation {
    String message() default "The time must be present or future !";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}