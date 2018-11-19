package org.oneuponcancer.redemption.model.transport;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class EventCreateRequestTest {
    private EventCreateRequest eventCreateRequest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        eventCreateRequest = new EventCreateRequest();
    }

    @Test
    public void testName() {
        eventCreateRequest.setName("Foop");

        assertEquals("Foop", eventCreateRequest.getName());
    }

    @Test
    public void testDescription() {
        eventCreateRequest.setDescription("A big bag of foop.");

        assertEquals("A big bag of foop.", eventCreateRequest.getDescription());
    }

    @Test
    public void testStartDate() {
        Date now = new Date();

        eventCreateRequest.setStartDate(now);

        assertEquals(now, eventCreateRequest.getStartDate());
    }

    @Test
    public void testEndDate() {
        Date now = new Date();

        eventCreateRequest.setEndDate(now);

        assertEquals(now, eventCreateRequest.getEndDate());
    }
}
