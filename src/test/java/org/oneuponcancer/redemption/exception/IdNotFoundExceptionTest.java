package org.oneuponcancer.redemption.exception;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class IdNotFoundExceptionTest {
    private UUID id = UUID.randomUUID();
    private String message = "Error message.";

    private IdNotFoundException exception;

    @Before
    public void setUp() {
        exception = new IdNotFoundException(id, message);
    }

    @Test
    public void testId() {
        assertEquals(id, exception.getId());
    }

    @Test
    public void testMessage() {
        assertEquals(message, exception.getMessage());
    }
}
