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
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        for (int i = 0; i < 3; i++) {
            allParticipants.add(mock(Participant.class));
        }

        when(participantRepository.findAll()).thenReturn(allParticipants);
        when(participantRepository.save(any(Participant.class))).thenAnswer(i -> {
            Participant participant = i.getArgumentAt(0, Participant.class);

            participant.setId(UUID.randomUUID().toString());

            return participant;
        });

        participantResource = new ParticipantResource(
                participantRepository,
                auditLogService);
    }

    @Test
    public void testFetchParticipant() throws Exception {
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.LIST_PARTICIPANT.name())));

        List<Participant> result = participantResource.fetchParticipant(principal);

        assertFalse(result.isEmpty());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testFetchParticipantNoPermission() throws Exception {
        participantResource.fetchParticipant(principal);
    }

    @Test
    public void testCreateParticipant() throws Exception {
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
        verify(auditLogService).log(anyString(), anyString(), anyString());

        Participant participant = participantArgumentCaptor.getValue();

        assertEquals("First", participant.getFirstName());
        assertEquals("Lasterson", participant.getLastName());
        assertEquals("first@lasterson.com", participant.getEmail());
        assertNotNull(participant.getId());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testCreateParticipantNoPermission() throws Exception {
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
    public void testCreateParticipantInvalidName() throws Exception {
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
    public void testCreateParticipantInvalidDescription() throws Exception {
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
    public void testUpdateParticipant() throws Exception {
        String id = "1";
        ParticipantEditRequest editRequest = mock(ParticipantEditRequest.class);
        Participant participant = mock(Participant.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_PARTICIPANT.name())));
        when(participantRepository.findOne(eq(id))).thenReturn(participant);
        when(editRequest.getFirstName()).thenReturn("First");
        when(editRequest.getLastName()).thenReturn("Lasterson");
        when(editRequest.getEmail()).thenReturn("first@lasterson.com");

        Participant response = participantResource.updateParticipant(
                id,
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
        verify(auditLogService).log(anyString(), anyString(), anyString());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testUpdateParticipantNoPermission() throws Exception {
        String id = "1";
        ParticipantEditRequest editRequest = mock(ParticipantEditRequest.class);
        Participant participant = mock(Participant.class);

        when(participantRepository.findOne(eq(id))).thenReturn(participant);
        when(editRequest.getFirstName()).thenReturn("First");
        when(editRequest.getLastName()).thenReturn("Lasterson");
        when(editRequest.getEmail()).thenReturn("first@lasterson.com");

        participantResource.updateParticipant(
                id,
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateParticipantNotFound() throws Exception {
        String id = "1";
        ParticipantEditRequest editRequest = mock(ParticipantEditRequest.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_PARTICIPANT.name())));
        when(editRequest.getFirstName()).thenReturn("First");
        when(editRequest.getLastName()).thenReturn("Lasterson");
        when(editRequest.getEmail()).thenReturn("first@lasterson.com");

        participantResource.updateParticipant(
                id,
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testUpdateParticipantBadName() throws Exception {
        String id = "1";
        ParticipantEditRequest editRequest = mock(ParticipantEditRequest.class);
        Participant participant = mock(Participant.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_PARTICIPANT.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid name.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(participantRepository.findOne(eq(id))).thenReturn(participant);
        when(editRequest.getFirstName()).thenReturn("First");
        when(editRequest.getLastName()).thenReturn("Lasterson");
        when(editRequest.getEmail()).thenReturn("first@lasterson.com");

        participantResource.updateParticipant(
                id,
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testUpdateParticipantBadDescription() throws Exception {
        String id = "1";
        ParticipantEditRequest editRequest = mock(ParticipantEditRequest.class);
        Participant participant = mock(Participant.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_PARTICIPANT.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid description.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(participantRepository.findOne(eq(id))).thenReturn(participant);
        when(editRequest.getFirstName()).thenReturn("First");
        when(editRequest.getLastName()).thenReturn("Lasterson");
        when(editRequest.getEmail()).thenReturn("first@lasterson.com");

        participantResource.updateParticipant(
                id,
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test
    public void testDeleteParticipant() throws Exception {
        String id = "id";
        Participant participant = mock(Participant.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.DELETE_PARTICIPANT.name())));
        when(participantRepository.findOne(eq(id))).thenReturn(participant);

        Participant result = participantResource.deleteParticipant(
                id,
                principal,
                request
        );

        assertEquals(participant, result);
        verify(participantRepository).findOne(eq(id));
        verify(participantRepository).delete(eq(participant));
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(anyString(), anyString(), anyString());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testDeleteParticipantNoPermission() throws Exception {
        String id = "id";

        participantResource.deleteParticipant(
                id,
                principal,
                request
        );
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteParticipantNotFound() throws Exception {
        String id = "id";

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.DELETE_PARTICIPANT.name())));

        participantResource.deleteParticipant(
                id,
                principal,
                request
        );
    }
}
