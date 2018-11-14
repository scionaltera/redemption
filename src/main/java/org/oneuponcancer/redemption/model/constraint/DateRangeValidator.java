package org.oneuponcancer.redemption.model.constraint;

import org.springframework.util.ReflectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Date;

public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {
    private String startDateFieldName;
    private String endDateFieldName;

    @Override
    public void initialize(DateRange constraintAnnotation) {
        this.startDateFieldName = constraintAnnotation.startDate();
        this.endDateFieldName = constraintAnnotation.endDate();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Field startDateField = ReflectionUtils.findField(value.getClass(), startDateFieldName);
        Field endDateField = ReflectionUtils.findField(value.getClass(), endDateFieldName);

        if (startDateField == null || endDateField == null) {
            return false;
        }

        ReflectionUtils.makeAccessible(startDateField);
        ReflectionUtils.makeAccessible(endDateField);

        Date startDate = (Date)ReflectionUtils.getField(startDateField, value);
        Date endDate = (Date)ReflectionUtils.getField(endDateField, value);

        if (startDate == null || endDate == null) {
            return false;
        }

        return !startDate.after(endDate);
    }
}
