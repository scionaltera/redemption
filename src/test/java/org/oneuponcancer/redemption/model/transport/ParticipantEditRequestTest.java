package org.oneuponcancer.redemption.model.transport;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class ParticipantEditRequestTest {
    private ParticipantEditRequest participantEditRequest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        participantEditRequest = new ParticipantEditRequest();
    }

    @Test
    public void testFirstName() throws Exception {
        participantEditRequest.setFirstName("First");

        assertEquals("First", participantEditRequest.getFirstName());
    }

    @Test
    public void testLastName() throws Exception {
        participantEditRequest.setLastName("Lasterson");

        assertEquals("Lasterson", participantEditRequest.getLastName());
    }

    @Test
    public void testEmail() throws Exception {
        participantEditRequest.setEmail("first@lasterson.com");

        assertEquals("first@lasterson.com", participantEditRequest.getEmail());
    }
}
