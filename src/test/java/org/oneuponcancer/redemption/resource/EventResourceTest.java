package org.oneuponcancer.redemption.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oneuponcancer.redemption.exception.InsufficientPermissionException;
import org.oneuponcancer.redemption.model.Event;
import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.model.transport.EventCreateRequest;
import org.oneuponcancer.redemption.model.transport.EventEditRequest;
import org.oneuponcancer.redemption.repository.EventRepository;
import org.oneuponcancer.redemption.service.AuditLogService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EventResourceTest {
    @Captor
    private ArgumentCaptor<Event> eventArgumentCaptor;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private UsernamePasswordAuthenticationToken principal;

    @Mock
    private HttpServletRequest request;

    private Date tomorrow = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
    private Date nextWeek = Date.from(Instant.now().plus(7, ChronoUnit.DAYS));
    private List<Event> allEvents = new ArrayList<>();

    private EventResource eventResource;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        for (int i = 0; i < 3; i++) {
            allEvents.add(mock(Event.class));
        }

        when(eventRepository.findAll()).thenReturn(allEvents);
        when(eventRepository.save(any(Event.class))).thenAnswer(i -> {
            Event event = i.getArgument(0);

            event.setId(UUID.randomUUID());

            return event;
        });

        eventResource = new EventResource(
                eventRepository,
                auditLogService);
    }

    @Test
    public void testFetchEvent() {
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.LIST_EVENT.name())));

        List<Event> result = eventResource.fetchEvent(principal);

        assertFalse(result.isEmpty());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testFetchEventNoPermission() {
        eventResource.fetchEvent(principal);
    }

    @Test
    public void testCreateEvent() {
        EventCreateRequest createRequest = new EventCreateRequest();

        createRequest.setName("Foop");
        createRequest.setDescription("A big bag of foop.");
        createRequest.setStartDate(tomorrow);
        createRequest.setEndDate(nextWeek);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.CREATE_EVENT.name())));

        Event response = eventResource.createEvent(
                createRequest,
                bindingResult,
                principal,
                request
        );

        assertNotNull(response);
        verify(eventRepository).save(eventArgumentCaptor.capture());
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(any(), any(), anyString());

        Event event = eventArgumentCaptor.getValue();

        assertNotNull(event.getId());
        assertEquals("Foop", event.getName());
        assertEquals("A big bag of foop.", event.getDescription());
        assertEquals(tomorrow, event.getStartDate());
        assertEquals(nextWeek, event.getEndDate());
        assertEquals(Collections.emptyList(), event.getParticipants());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testCreateEventNoPermission() {
        EventCreateRequest createRequest = mock(EventCreateRequest.class);

        when(createRequest.getName()).thenReturn("Foop");
        when(createRequest.getDescription()).thenReturn("A big bag of foop.");

        eventResource.createEvent(
                createRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testCreateEventInvalidName() {
        EventCreateRequest createRequest = mock(EventCreateRequest.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.CREATE_EVENT.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid name.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(createRequest.getName()).thenReturn("");
        when(createRequest.getDescription()).thenReturn("A big bag of foop.");
        when(createRequest.getStartDate()).thenReturn(tomorrow);
        when(createRequest.getEndDate()).thenReturn(nextWeek);

        eventResource.createEvent(
                createRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testCreateEventInvalidDescription() {
        EventCreateRequest createRequest = mock(EventCreateRequest.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.CREATE_EVENT.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid description.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(createRequest.getName()).thenReturn("Foop");
        when(createRequest.getDescription()).thenReturn("");
        when(createRequest.getStartDate()).thenReturn(tomorrow);
        when(createRequest.getEndDate()).thenReturn(nextWeek);

        eventResource.createEvent(
                createRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testCreateEventInvalidStartDate() {
        EventCreateRequest createRequest = mock(EventCreateRequest.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.CREATE_EVENT.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid description.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(createRequest.getName()).thenReturn("Foop");
        when(createRequest.getDescription()).thenReturn("A big bag of foop.");
        when(createRequest.getStartDate()).thenReturn(null);
        when(createRequest.getEndDate()).thenReturn(nextWeek);

        eventResource.createEvent(
                createRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testCreateEventInvalidEndDate() {
        EventCreateRequest createRequest = mock(EventCreateRequest.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.CREATE_EVENT.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid description.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(createRequest.getName()).thenReturn("Foop");
        when(createRequest.getDescription()).thenReturn("A big bag of foop.");
        when(createRequest.getStartDate()).thenReturn(tomorrow);
        when(createRequest.getEndDate()).thenReturn(null);

        eventResource.createEvent(
                createRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test
    public void testUpdateEvent() {
        UUID uuid = UUID.randomUUID();
        EventEditRequest editRequest = new EventEditRequest();
        Event event = new Event();

        event.setParticipants(Collections.emptyList());

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_EVENT.name())));
        when(eventRepository.findById(eq(uuid))).thenReturn(Optional.of(event));

        editRequest.setName("Foop");
        editRequest.setDescription("A big bag of foop.");
        editRequest.setStartDate(tomorrow);
        editRequest.setEndDate(nextWeek);

        Event response = eventResource.updateEvent(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );

        assertNotNull(response);
        assertEquals("Foop", event.getName());
        assertEquals("A big bag of foop.", event.getDescription());
        assertEquals(tomorrow, event.getStartDate());
        assertEquals(nextWeek, event.getEndDate());
        assertNotNull(event.getParticipants());

        verify(eventRepository).save(eq(event));
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(any(), any(), anyString());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testUpdateEventNoPermission() {
        UUID uuid = UUID.randomUUID();
        EventEditRequest editRequest = mock(EventEditRequest.class);
        Event event = mock(Event.class);

        when(eventRepository.findById(eq(uuid))).thenReturn(Optional.of(event));
        when(editRequest.getName()).thenReturn("Carp");
        when(editRequest.getDescription()).thenReturn("A bucket of carp.");
        when(editRequest.getStartDate()).thenReturn(tomorrow);
        when(editRequest.getEndDate()).thenReturn(nextWeek);

        eventResource.updateEvent(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateEventNotFound() {
        UUID uuid = UUID.randomUUID();
        EventEditRequest editRequest = mock(EventEditRequest.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_EVENT.name())));
        when(editRequest.getName()).thenReturn("Carp");
        when(editRequest.getDescription()).thenReturn("A bucket of carp.");
        when(editRequest.getStartDate()).thenReturn(tomorrow);
        when(editRequest.getEndDate()).thenReturn(nextWeek);

        eventResource.updateEvent(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testUpdateEventBadName() {
        UUID uuid = UUID.randomUUID();
        EventEditRequest editRequest = mock(EventEditRequest.class);
        Event event = mock(Event.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_EVENT.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid name.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(eventRepository.findById(eq(uuid))).thenReturn(Optional.of(event));
        when(editRequest.getName()).thenReturn("");
        when(editRequest.getDescription()).thenReturn("A bucket of carp.");
        when(editRequest.getStartDate()).thenReturn(tomorrow);
        when(editRequest.getEndDate()).thenReturn(nextWeek);

        eventResource.updateEvent(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testUpdateEventBadDescription() {
        UUID uuid = UUID.randomUUID();
        EventEditRequest editRequest = mock(EventEditRequest.class);
        Event event = mock(Event.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_EVENT.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid description.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(eventRepository.findById(eq(uuid))).thenReturn(Optional.of(event));
        when(editRequest.getName()).thenReturn("Carp");
        when(editRequest.getDescription()).thenReturn("");
        when(editRequest.getStartDate()).thenReturn(tomorrow);
        when(editRequest.getEndDate()).thenReturn(nextWeek);

        eventResource.updateEvent(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testUpdateEventBadStartDate() {
        UUID uuid = UUID.randomUUID();
        EventEditRequest editRequest = mock(EventEditRequest.class);
        Event event = mock(Event.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_EVENT.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid description.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(eventRepository.findById(eq(uuid))).thenReturn(Optional.of(event));
        when(editRequest.getName()).thenReturn("Carp");
        when(editRequest.getDescription()).thenReturn("A bucket of carp.");
        when(editRequest.getStartDate()).thenReturn(null);
        when(editRequest.getEndDate()).thenReturn(nextWeek);

        eventResource.updateEvent(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testUpdateEventBadEndDate() {
        UUID uuid = UUID.randomUUID();
        EventEditRequest editRequest = mock(EventEditRequest.class);
        Event event = mock(Event.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_EVENT.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid description.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(eventRepository.findById(eq(uuid))).thenReturn(Optional.of(event));
        when(editRequest.getName()).thenReturn("Carp");
        when(editRequest.getDescription()).thenReturn("A bucket of carp.");
        when(editRequest.getStartDate()).thenReturn(tomorrow);
        when(editRequest.getEndDate()).thenReturn(null);

        eventResource.updateEvent(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test
    public void testDeleteEvent() {
        UUID uuid = UUID.randomUUID();
        Event event = mock(Event.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.DELETE_EVENT.name())));
        when(eventRepository.findById(eq(uuid))).thenReturn(Optional.of(event));

        Event result = eventResource.deleteEvent(
                uuid.toString(),
                principal,
                request
        );

        assertEquals(event, result);
        verify(eventRepository).findById(eq(uuid));
        verify(eventRepository).delete(eq(event));
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(any(), any(), anyString());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testDeleteEventNoPermission() {
        UUID uuid = UUID.randomUUID();

        eventResource.deleteEvent(
                uuid.toString(),
                principal,
                request
        );
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteEventNotFound() {
        UUID uuid = UUID.randomUUID();

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.DELETE_EVENT.name())));

        eventResource.deleteEvent(
                uuid.toString(),
                principal,
                request
        );
    }
}
