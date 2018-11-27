package org.oneuponcancer.redemption.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oneuponcancer.redemption.exception.InsufficientPermissionException;
import org.oneuponcancer.redemption.model.Asset;
import org.oneuponcancer.redemption.model.Award;
import org.oneuponcancer.redemption.model.AwardIdentity;
import org.oneuponcancer.redemption.model.Event;
import org.oneuponcancer.redemption.model.Participant;
import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.model.transport.AwardAssetChangeRequest;
import org.oneuponcancer.redemption.model.transport.EventAddParticipantRequest;
import org.oneuponcancer.redemption.repository.AssetRepository;
import org.oneuponcancer.redemption.repository.AwardRepository;
import org.oneuponcancer.redemption.repository.EventRepository;
import org.oneuponcancer.redemption.repository.ParticipantRepository;
import org.oneuponcancer.redemption.service.AuditLogService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class EventParticipantResourceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AwardRepository awardRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private UsernamePasswordAuthenticationToken principal;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Captor
    private ArgumentCaptor<Award> awardCaptor;

    private String participantEmail = "mark@twain.com";
    private Participant participant = new Participant();
    private UUID eventId = UUID.randomUUID();
    private UUID assetId = UUID.randomUUID();
    private Event event = new Event();
    private Asset asset = new Asset();
    private Award award = new Award();

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

        when(awardRepository.save(any(Award.class))).thenAnswer(i -> i.getArgument(0));

        participant.setId(UUID.randomUUID());
        participant.setEmail(participantEmail);
        participant.setFirstName("Mark");
        participant.setLastName("Twain");

        event.setId(eventId);
        event.setName("Test Event");

        asset.setId(assetId);
        asset.setName("Shiny");
        asset.setEvent(event);

        AwardIdentity awardIdentity = new AwardIdentity();

        awardIdentity.setEvent(event);
        awardIdentity.setParticipant(participant);

        award.setAwardIdentity(awardIdentity);

        when(principal.getName()).thenReturn("Admin");
        when(auditLogService.extractRemoteIp(any(HttpServletRequest.class))).thenReturn("500.501.502.503");
        when(participantRepository.findByEmail(eq(participantEmail))).thenReturn(Optional.of(participant));
        when(participantRepository.findById(eq(participant.getId()))).thenReturn(Optional.of(participant));
        when(eventRepository.findById(eq(event.getId()))).thenReturn(Optional.of(event));
        when(assetRepository.findById(eq(asset.getId()))).thenReturn(Optional.of(asset));
        when(awardRepository.findByAwardIdentity_EventAndAwardIdentity_Participant(eq(event), eq(participant))).thenReturn(Optional.of(award));
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_EVENT.name())));

        resource = new EventParticipantResource(
                eventRepository,
                participantRepository,
                assetRepository,
                awardRepository,
                auditLogService
        );
    }

    @Test
    public void testAddParticipant() {
        EventAddParticipantRequest request = new EventAddParticipantRequest();

        request.setEmail(participantEmail);

        Map<String, Object> result = resource.addParticipant(
                request,
                bindingResult,
                eventId,
                principal,
                httpServletRequest);

        assertEquals(participant, result.get("participant"));
        assertEquals(eventId, result.get("eventId"));

        verify(awardRepository).save(awardCaptor.capture());
        verify(auditLogService).log(anyString(), anyString(), contains("Added participant"));

        Award award = awardCaptor.getValue();

        assertEquals(event, award.getAwardIdentity().getEvent());
        assertEquals(participant, award.getAwardIdentity().getParticipant());
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

    @Test
    public void testRemoveParticipant() {
        resource.removeParticipant(
                eventId,
                participant.getId(),
                principal,
                httpServletRequest);

        verify(awardRepository).delete(award);
        verify(auditLogService).log(anyString(), anyString(), contains("Removed participant"));
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testRemoveParticipantNoPermission() {
        when(principal.getAuthorities()).thenReturn(Collections.emptyList());

        resource.removeParticipant(
                eventId,
                participant.getId(),
                principal,
                httpServletRequest);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveParticipantNoSuchEvent() {
        resource.removeParticipant(
                UUID.randomUUID(),
                participant.getId(),
                principal,
                httpServletRequest);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveParticipantNoSuchParticipant() {
        resource.removeParticipant(
                eventId,
                UUID.randomUUID(),
                principal,
                httpServletRequest);
    }

    @Test
    public void testAssignAsset() {
        AwardAssetChangeRequest changeRequest = new AwardAssetChangeRequest();

        changeRequest.setAssetId(assetId);

        resource.assignAsset(
                eventId,
                participant.getId(),
                changeRequest,
                principal,
                httpServletRequest);

        verify(awardRepository).save(awardCaptor.capture());
        verify(auditLogService).log(anyString(), anyString(), anyString());

        Award award = awardCaptor.getValue();

        assertEquals(asset, award.getAsset());
    }

    @Test
    public void testAssignAssetNone() {
        AwardAssetChangeRequest changeRequest = new AwardAssetChangeRequest();

        changeRequest.setAssetId(null);

        resource.assignAsset(
                eventId,
                participant.getId(),
                changeRequest,
                principal,
                httpServletRequest);

        verify(awardRepository).save(awardCaptor.capture());
        verify(auditLogService).log(anyString(), anyString(), anyString());

        Award award = awardCaptor.getValue();

        assertNull(award.getAsset());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testAssignAssetNoPermission() {
        AwardAssetChangeRequest changeRequest = new AwardAssetChangeRequest();

        changeRequest.setAssetId(assetId);

        when(principal.getAuthorities()).thenReturn(Collections.emptyList());

        resource.assignAsset(
                eventId,
                participant.getId(),
                changeRequest,
                principal,
                httpServletRequest);
    }

    @Test(expected = NullPointerException.class)
    public void testAssignAssetNoEvent() {
        AwardAssetChangeRequest changeRequest = new AwardAssetChangeRequest();

        changeRequest.setAssetId(assetId);

        when(eventRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        resource.assignAsset(
                eventId,
                participant.getId(),
                changeRequest,
                principal,
                httpServletRequest);
    }

    @Test(expected = NullPointerException.class)
    public void testAssignAssetNoParticipant() {
        AwardAssetChangeRequest changeRequest = new AwardAssetChangeRequest();

        changeRequest.setAssetId(assetId);

        resource.assignAsset(
                eventId,
                UUID.randomUUID(),
                changeRequest,
                principal,
                httpServletRequest);
    }

    @Test(expected = NullPointerException.class)
    public void testAssignAssetNoAsset() {
        AwardAssetChangeRequest changeRequest = new AwardAssetChangeRequest();

        changeRequest.setAssetId(assetId);

        when(assetRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        resource.assignAsset(
                eventId,
                participant.getId(),
                changeRequest,
                principal,
                httpServletRequest);
    }

    @Test(expected = NullPointerException.class)
    public void testAssignAssetNoAward() {
        AwardAssetChangeRequest changeRequest = new AwardAssetChangeRequest();

        changeRequest.setAssetId(assetId);

        when(awardRepository.findByAwardIdentity_EventAndAwardIdentity_Participant(any(Event.class), any(Participant.class))).thenReturn(Optional.empty());

        resource.assignAsset(
                eventId,
                participant.getId(),
                changeRequest,
                principal,
                httpServletRequest);
    }
}
