package org.oneuponcancer.redemption.model.transport;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class ParticipantCreateRequestTest {
    private ParticipantCreateRequest participantCreateRequest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        participantCreateRequest = new ParticipantCreateRequest();
    }

    @Test
    public void testFirstName() throws Exception {
        participantCreateRequest.setFirstName("First");

        assertEquals("First", participantCreateRequest.getFirstName());
    }

    @Test
    public void testLastName() throws Exception {
        participantCreateRequest.setLastName("Lasterson");

        assertEquals("Lasterson", participantCreateRequest.getLastName());
    }

    @Test
    public void testEmail() throws Exception {
        participantCreateRequest.setEmail("first@lasterson.com");

        assertEquals("first@lasterson.com", participantCreateRequest.getEmail());
    }
}
