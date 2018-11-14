package org.oneuponcancer.redemption.model.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface DateRange {
    String message() default "Start date must be earlier than end date.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String startDate();
    String endDate();
}
