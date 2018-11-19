package org.oneuponcancer.redemption.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oneuponcancer.redemption.exception.InsufficientPermissionException;
import org.oneuponcancer.redemption.model.Event;
import org.oneuponcancer.redemption.model.Participant;
import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.model.transport.EventAddParticipantRequest;
import org.oneuponcancer.redemption.repository.EventRepository;
import org.oneuponcancer.redemption.repository.ParticipantRepository;
import org.oneuponcancer.redemption.service.AuditLogService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class EventParticipantResourceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private UsernamePasswordAuthenticationToken principal;

    @Mock
    private HttpServletRequest httpServletRequest;

    private String participantEmail = "mark@twain.com";
    private Participant participant = new Participant();
    private UUID eventId = UUID.randomUUID();
    private Event event = new Event();

    private EventParticipantResource resource;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(eventRepository.save(any(Event.class))).thenAnswer(i -> {
            Event event = i.getArgument(0);

            if (event.getId() == null) {
                event.setId(UUID.randomUUID());
            }

            return event;
        });

        participant.setId(UUID.randomUUID());
        participant.setEmail(participantEmail);
        participant.setFirstName("Mark");
        participant.setLastName("Twain");

        event.setId(eventId);
        event.setName("Test Event");
        event.setParticipants(new ArrayList<>());

        when(principal.getName()).thenReturn("Admin");
        when(auditLogService.extractRemoteIp(any(HttpServletRequest.class))).thenReturn("500.501.502.503");
        when(participantRepository.findByEmail(eq(participantEmail))).thenReturn(Optional.of(participant));
        when(eventRepository.findById(eq(event.getId()))).thenReturn(Optional.of(event));
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_EVENT.name())));

        resource = new EventParticipantResource(
                eventRepository,
                participantRepository,
                auditLogService
        );
    }

    @Test
    public void testAddParticipant() {
        EventAddParticipantRequest request = new EventAddParticipantRequest();

        request.setEmail(participantEmail);

        Event result = resource.addParticipant(
                request,
                bindingResult,
                eventId,
                principal,
                httpServletRequest);

        assertTrue(result.getParticipants().contains(participant));

        verify(auditLogService).log(anyString(), anyString(), contains("Added participant"));
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testAddParticipantNoPermission() {
        EventAddParticipantRequest request = new EventAddParticipantRequest();

        request.setEmail(participantEmail);

        when(principal.getAuthorities()).thenReturn(Collections.emptyList());

        resource.addParticipant(
                request,
                bindingResult,
                eventId,
                principal,
                httpServletRequest);
    }

    @Test(expected = ValidationException.class)
    public void testAddParticipantMalformedEmail() {
        String notAnEmail = "i-am-not-an-email-address";
        EventAddParticipantRequest request = new EventAddParticipantRequest();
        ObjectError objectError = mock(ObjectError.class);

        request.setEmail(notAnEmail);

        when(objectError.getDefaultMessage()).thenReturn("Not a valid email address.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));

        resource.addParticipant(
                request,
                bindingResult,
                eventId,
                principal,
                httpServletRequest);
    }

    @Test(expected = NullPointerException.class)
    public void testAddParticipantNoSuchParticipant() {
        String nonExistentEmail = "no.such@email.address";
        EventAddParticipantRequest request = new EventAddParticipantRequest();

        request.setEmail(nonExistentEmail);

        resource.addParticipant(
                request,
                bindingResult,
                eventId,
                principal,
                httpServletRequest);
    }

    @Test(expected = NullPointerException.class)
    public void testAddParticipantNoSuchEvent() {
        UUID badEventId = UUID.randomUUID();
        EventAddParticipantRequest request = new EventAddParticipantRequest();

        request.setEmail(participantEmail);

        resource.addParticipant(
                request,
                bindingResult,
                badEventId,
                principal,
                httpServletRequest);
    }
}
