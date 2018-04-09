package org.oneuponcancer.redemption.model.transport;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class EventEditRequestTest {
    private EventEditRequest eventEditRequest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        eventEditRequest = new EventEditRequest();
    }

    @Test
    public void testName() {
        eventEditRequest.setName("Foop");

        assertEquals("Foop", eventEditRequest.getName());
    }

    @Test
    public void testDescription() {
        eventEditRequest.setDescription("A big bag of foop.");

        assertEquals("A big bag of foop.", eventEditRequest.getDescription());
    }

    @Test
    public void testStartDate() {
        Date now = new Date();

        eventEditRequest.setStartDate(now);

        assertEquals(now, eventEditRequest.getStartDate());
    }

    @Test
    public void testEndDate() {
        Date now = new Date();

        eventEditRequest.setEndDate(now);

        assertEquals(now, eventEditRequest.getEndDate());
    }
}
