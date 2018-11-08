package org.oneuponcancer.redemption.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oneuponcancer.redemption.exception.InsufficientPermissionException;
import org.oneuponcancer.redemption.model.Participant;
import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.model.transport.ParticipantCreateRequest;
import org.oneuponcancer.redemption.model.transport.ParticipantEditRequest;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ParticipantResourceTest {
    @Captor
    private ArgumentCaptor<Participant> participantArgumentCaptor;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private UsernamePasswordAuthenticationToken principal;

    @Mock
    private HttpServletRequest request;

    private List<Participant> allParticipants = new ArrayList<>();

    private ParticipantResource participantResource;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        for (int i = 0; i < 3; i++) {
            allParticipants.add(mock(Participant.class));
        }

        when(participantRepository.findAll()).thenReturn(allParticipants);
        when(participantRepository.save(any(Participant.class))).thenAnswer(i -> {
            Participant participant = i.getArgument(0);

            participant.setId(UUID.randomUUID());

            return participant;
        });

        participantResource = new ParticipantResource(
                participantRepository,
                auditLogService);
    }

    @Test
    public void testFetchParticipant() {
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.LIST_PARTICIPANT.name())));

        List<Participant> result = participantResource.fetchParticipant(principal);

        assertFalse(result.isEmpty());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testFetchParticipantNoPermission() {
        participantResource.fetchParticipant(principal);
    }

    @Test
    public void testCreateParticipant() {
        ParticipantCreateRequest createRequest = mock(ParticipantCreateRequest.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.CREATE_PARTICIPANT.name())));
        when(createRequest.getFirstName()).thenReturn("First");
        when(createRequest.getLastName()).thenReturn("Lasterson");
        when(createRequest.getEmail()).thenReturn("first@lasterson.com");

        Participant response = participantResource.createParticipant(
                createRequest,
                bindingResult,
                principal,
                request
        );

        assertNotNull(response);
        verify(participantRepository).save(participantArgumentCaptor.capture());
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(any(), any(), anyString());

        Participant participant = participantArgumentCaptor.getValue();

        assertEquals("First", participant.getFirstName());
        assertEquals("Lasterson", participant.getLastName());
        assertEquals("first@lasterson.com", participant.getEmail());
        assertNotNull(participant.getId());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testCreateParticipantNoPermission() {
        ParticipantCreateRequest createRequest = mock(ParticipantCreateRequest.class);

        when(createRequest.getFirstName()).thenReturn("First");
        when(createRequest.getLastName()).thenReturn("Lasterson");
        when(createRequest.getEmail()).thenReturn("first@lasterson.com");

        participantResource.createParticipant(
                createRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testCreateParticipantInvalidName() {
        ParticipantCreateRequest createRequest = mock(ParticipantCreateRequest.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.CREATE_PARTICIPANT.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid name.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(createRequest.getFirstName()).thenReturn("First");
        when(createRequest.getLastName()).thenReturn("Lasterson");
        when(createRequest.getEmail()).thenReturn("first@lasterson.com");

        participantResource.createParticipant(
                createRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testCreateParticipantInvalidDescription() {
        ParticipantCreateRequest createRequest = mock(ParticipantCreateRequest.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.CREATE_PARTICIPANT.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid description.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(createRequest.getFirstName()).thenReturn("First");
        when(createRequest.getLastName()).thenReturn("Lasterson");
        when(createRequest.getEmail()).thenReturn("first@lasterson.com");

        participantResource.createParticipant(
                createRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test
    public void testUpdateParticipant() {
        UUID uuid = UUID.randomUUID();
        ParticipantEditRequest editRequest = mock(ParticipantEditRequest.class);
        Participant participant = mock(Participant.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_PARTICIPANT.name())));
        when(participantRepository.findById(eq(uuid))).thenReturn(Optional.of(participant));
        when(editRequest.getFirstName()).thenReturn("First");
        when(editRequest.getLastName()).thenReturn("Lasterson");
        when(editRequest.getEmail()).thenReturn("first@lasterson.com");

        Participant response = participantResource.updateParticipant(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );

        assertNotNull(response);
        verify(participant).setFirstName(eq("First"));
        verify(participant).setLastName(eq("Lasterson"));
        verify(participant).setEmail(eq("first@lasterson.com"));
        verify(participantRepository).save(eq(participant));
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(any(), any(), anyString());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testUpdateParticipantNoPermission() {
        UUID uuid = UUID.randomUUID();
        ParticipantEditRequest editRequest = mock(ParticipantEditRequest.class);
        Participant participant = mock(Participant.class);

        when(participantRepository.findById(eq(uuid))).thenReturn(Optional.of(participant));
        when(editRequest.getFirstName()).thenReturn("First");
        when(editRequest.getLastName()).thenReturn("Lasterson");
        when(editRequest.getEmail()).thenReturn("first@lasterson.com");

        participantResource.updateParticipant(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateParticipantNotFound() {
        UUID uuid = UUID.randomUUID();
        ParticipantEditRequest editRequest = mock(ParticipantEditRequest.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_PARTICIPANT.name())));
        when(editRequest.getFirstName()).thenReturn("First");
        when(editRequest.getLastName()).thenReturn("Lasterson");
        when(editRequest.getEmail()).thenReturn("first@lasterson.com");

        participantResource.updateParticipant(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testUpdateParticipantBadName() {
        UUID uuid = UUID.randomUUID();
        ParticipantEditRequest editRequest = mock(ParticipantEditRequest.class);
        Participant participant = mock(Participant.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_PARTICIPANT.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid name.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(participantRepository.findById(eq(uuid))).thenReturn(Optional.of(participant));
        when(editRequest.getFirstName()).thenReturn("First");
        when(editRequest.getLastName()).thenReturn("Lasterson");
        when(editRequest.getEmail()).thenReturn("first@lasterson.com");

        participantResource.updateParticipant(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testUpdateParticipantBadDescription() {
        UUID uuid = UUID.randomUUID();
        ParticipantEditRequest editRequest = mock(ParticipantEditRequest.class);
        Participant participant = mock(Participant.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_PARTICIPANT.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid description.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(participantRepository.findById(eq(uuid))).thenReturn(Optional.of(participant));
        when(editRequest.getFirstName()).thenReturn("First");
        when(editRequest.getLastName()).thenReturn("Lasterson");
        when(editRequest.getEmail()).thenReturn("first@lasterson.com");

        participantResource.updateParticipant(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test
    public void testDeleteParticipant() {
        UUID uuid = UUID.randomUUID();
        Participant participant = mock(Participant.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.DELETE_PARTICIPANT.name())));
        when(participantRepository.findById(eq(uuid))).thenReturn(Optional.of(participant));

        Participant result = participantResource.deleteParticipant(
                uuid.toString(),
                principal,
                request
        );

        assertEquals(participant, result);
        verify(participantRepository).findById(eq(uuid));
        verify(participantRepository).delete(eq(participant));
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(any(), any(), anyString());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testDeleteParticipantNoPermission() {
        UUID uuid = UUID.randomUUID();

        participantResource.deleteParticipant(
                uuid.toString(),
                principal,
                request
        );
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteParticipantNotFound() {
        UUID uuid = UUID.randomUUID();

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.DELETE_PARTICIPANT.name())));

        participantResource.deleteParticipant(
                uuid.toString(),
                principal,
                request
        );
    }
}
