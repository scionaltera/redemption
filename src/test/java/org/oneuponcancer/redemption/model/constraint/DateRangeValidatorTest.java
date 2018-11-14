package org.oneuponcancer.redemption.model.constraint;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Annotation;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DateRangeValidatorTest {
    @Mock
    private ConstraintValidatorContext context;

    private DateRangeValidator validator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        validator = new DateRangeValidator();
    }

    @Test
    public void testNullDates() {
        validator.initialize(new TestDateRange());

        Model model = new Model();
        model.setStartDate(null);
        model.setEndDate(null);

        assertFalse(validator.isValid(model, context));
    }

    @Test
    public void testNullStartDate() {
        validator.initialize(new TestDateRange());

        Model model = new Model();
        model.setStartDate(null);
        model.setEndDate(new Date(1542176811L));

        assertFalse(validator.isValid(model, context));
    }

    @Test
    public void testNullEndDate() {
        validator.initialize(new TestDateRange());

        Model model = new Model();
        model.setStartDate(new Date(1542176811L));
        model.setEndDate(null);

        assertFalse(validator.isValid(model, context));
    }

    @Test
    public void testStartAfterEndDate() {
        validator.initialize(new TestDateRange());

        Model model = new Model();
        model.setStartDate(new Date(1542176876L));
        model.setEndDate(new Date(1542176811L));

        assertFalse(validator.isValid(model, context));
    }

    @Test
    public void testStartEqualsEndDate() {
        validator.initialize(new TestDateRange());

        Model model = new Model();
        model.setStartDate(new Date(1542176876L));
        model.setEndDate(new Date(1542176876L));

        assertTrue(validator.isValid(model, context));
    }

    @Test
    public void testStartBeforeEndDate() {
        validator.initialize(new TestDateRange());

        Model model = new Model();
        model.setStartDate(new Date(1542176811L));
        model.setEndDate(new Date(1542176876L));

        assertTrue(validator.isValid(model, context));
    }

    @Test
    public void testIncorrectStartDateFieldName() {
        validator.initialize(new DateRange() {
            @Override
            public String message() {
                return "Incorrect date range.";
            }

            @Override
            public Class<?>[] groups() {
                return null;
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return null;
            }

            @Override
            public String startDate() {
                return "wrongStartDateField";
            }

            @Override
            public String endDate() {
                return "endDate";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return DateRange.class;
            }
        });

        Model model = new Model();
        model.setStartDate(new Date(1542176811L));
        model.setEndDate(new Date(1542176876L));

        assertFalse(validator.isValid(model, context));
    }

    @Test
    public void testIncorrectEndDateFieldName() {
        validator.initialize(new DateRange() {
            @Override
            public String message() {
                return "Incorrect date range.";
            }

            @Override
            public Class<?>[] groups() {
                return null;
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return null;
            }

            @Override
            public String startDate() {
                return "startDate";
            }

            @Override
            public String endDate() {
                return "wrongEndDateField";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return DateRange.class;
            }
        });

        Model model = new Model();
        model.setStartDate(new Date(1542176811L));
        model.setEndDate(new Date(1542176876L));

        assertFalse(validator.isValid(model, context));
    }

    private static class Model {
        private Date startDate;
        private Date endDate;

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }
    }

    private static class TestDateRange implements DateRange {
        @Override
        public String message() {
            return "Date range is incorrect.";
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return DateRange.class;
        }

        @Override
        public Class<?>[] groups() {
            return null;
        }

        @Override
        public Class<? extends Payload>[] payload() {
            return null;
        }

        @Override
        public String startDate() {
            return "startDate";
        }

        @Override
        public String endDate() {
            return "endDate";
        }
    }
}
