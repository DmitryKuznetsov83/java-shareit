package ru.practicum.shareit.booking.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StartEndFieldsValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StartEndFields {
	String message() default "Invalid start-end pair";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
