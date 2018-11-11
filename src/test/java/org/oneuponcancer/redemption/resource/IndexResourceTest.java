package org.oneuponcancer.redemption.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oneuponcancer.redemption.exception.InsufficientPermissionException;
import org.oneuponcancer.redemption.loader.StaffLoader;
import org.oneuponcancer.redemption.model.Asset;
import org.oneuponcancer.redemption.model.Event;
import org.oneuponcancer.redemption.model.Participant;
import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.model.Staff;
import org.oneuponcancer.redemption.repository.AssetRepository;
import org.oneuponcancer.redemption.repository.EventRepository;
import org.oneuponcancer.redemption.repository.ParticipantRepository;
import org.oneuponcancer.redemption.repository.StaffRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class IndexResourceTest {
    private static final String APPLICATION_VERSION = "0.0.0";

    @Mock
    private UsernamePasswordAuthenticationToken principal;

    @Mock
    private Model model;

    @Mock
    private StaffLoader staffLoader;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private Staff staff;

    @Mock
    private Asset asset;

    @Mock
    private Event event;

    @Mock
    private Participant participant;

    private IndexResource indexResource;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        indexResource = new IndexResource(
                APPLICATION_VERSION,
                staffLoader,
                staffRepository,
                assetRepository,
                participantRepository,
                eventRepository);
    }

    @Test
    public void testIndexNullPrincipal() {
        String result = indexResource.index(null, model);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));

        assertEquals("index", result);
    }

    @Test
    public void testIndexPrincipal() {
        String result = indexResource.index(principal, model);

        assertEquals("redirect:/dashboard", result);
    }

    @Test
    public void testLoginNullParams() {
        String result = indexResource.login(null, null, model);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verifyNoMoreInteractions(model);

        assertEquals("index", result);
    }

    @Test
    public void testLoginLogout() {
        String result = indexResource.login("logged out", null, model);

        verify(model).addAttribute(eq("message"), contains("logged out"));
        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verifyNoMoreInteractions(model);

        assertEquals("index", result);
    }

    @Test
    public void testLoginError() {
        String result = indexResource.login(null, "credentials", model);

        verify(model).addAttribute(eq("message"), contains("credentials"));
        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verifyNoMoreInteractions(model);

        assertEquals("index", result);
    }

    @Test
    public void testDashboard() {
        when(principal.getName()).thenReturn("admin");
        when(principal.getAuthorities()).thenReturn(Arrays.asList(
                new SimpleGrantedAuthority(Permission.LIST_STAFF.name()),
                new SimpleGrantedAuthority(Permission.EDIT_STAFF.name()),
                new SimpleGrantedAuthority(Permission.CREATE_STAFF.name()),
                new SimpleGrantedAuthority(Permission.DELETE_STAFF.name()),
                new SimpleGrantedAuthority(Permission.READ_LOGS.name())
        ));
        when(staffRepository.findByUsername(eq("admin"))).thenReturn(Optional.of(staff));

        String result = indexResource.dashboard(principal, model);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verify(model).addAttribute(eq("secure"), anyBoolean());
        verify(model).addAttribute(eq("staff"), eq(staff));
        verify(model).addAttribute(eq("list-staff"), eq(true));
        verify(model).addAttribute(eq("create-staff"), eq(true));
        verify(model).addAttribute(eq("edit-staff"), eq(true));
        verify(model).addAttribute(eq("delete-staff"), eq(true));
        verify(model).addAttribute(eq("read-logs"), eq(true));

        assertEquals("dashboard", result);
    }

    @Test
    public void testDashboardNoListStaffPermission() {
        when(principal.getName()).thenReturn("admin");
        when(principal.getAuthorities()).thenReturn(Arrays.asList(
                new SimpleGrantedAuthority(Permission.EDIT_STAFF.name()),
                new SimpleGrantedAuthority(Permission.CREATE_STAFF.name()),
                new SimpleGrantedAuthority(Permission.DELETE_STAFF.name()),
                new SimpleGrantedAuthority(Permission.READ_LOGS.name())
        ));
        when(staffRepository.findByUsername(eq("admin"))).thenReturn(Optional.of(staff));

        String result = indexResource.dashboard(principal, model);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verify(model).addAttribute(eq("secure"), anyBoolean());
        verify(model).addAttribute(eq("staff"), eq(staff));
        verify(model, never()).addAttribute(eq("list-staff"), eq(true));
        verify(model).addAttribute(eq("create-staff"), eq(true));
        verify(model).addAttribute(eq("edit-staff"), eq(true));
        verify(model).addAttribute(eq("delete-staff"), eq(true));
        verify(model).addAttribute(eq("read-logs"), eq(true));

        assertEquals("dashboard", result);
    }

    @Test
    public void testDashboardNoCreateStaffPermission() {
        when(principal.getName()).thenReturn("admin");
        when(principal.getAuthorities()).thenReturn(Arrays.asList(
                new SimpleGrantedAuthority(Permission.LIST_STAFF.name()),
                new SimpleGrantedAuthority(Permission.EDIT_STAFF.name()),
                new SimpleGrantedAuthority(Permission.DELETE_STAFF.name()),
                new SimpleGrantedAuthority(Permission.READ_LOGS.name())
        ));
        when(staffRepository.findByUsername(eq("admin"))).thenReturn(Optional.of(staff));

        String result = indexResource.dashboard(principal, model);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verify(model).addAttribute(eq("secure"), anyBoolean());
        verify(model).addAttribute(eq("staff"), eq(staff));
        verify(model).addAttribute(eq("list-staff"), eq(true));
        verify(model, never()).addAttribute(eq("create-staff"), eq(true));
        verify(model).addAttribute(eq("edit-staff"), eq(true));
        verify(model).addAttribute(eq("delete-staff"), eq(true));
        verify(model).addAttribute(eq("read-logs"), eq(true));

        assertEquals("dashboard", result);
    }

    @Test
    public void testDashboardNoEditStaffPermission() {
        when(principal.getName()).thenReturn("admin");
        when(principal.getAuthorities()).thenReturn(Arrays.asList(
                new SimpleGrantedAuthority(Permission.LIST_STAFF.name()),
                new SimpleGrantedAuthority(Permission.CREATE_STAFF.name()),
                new SimpleGrantedAuthority(Permission.DELETE_STAFF.name()),
                new SimpleGrantedAuthority(Permission.READ_LOGS.name())
        ));
        when(staffRepository.findByUsername(eq("admin"))).thenReturn(Optional.of(staff));

        String result = indexResource.dashboard(principal, model);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verify(model).addAttribute(eq("secure"), anyBoolean());
        verify(model).addAttribute(eq("staff"), eq(staff));
        verify(model).addAttribute(eq("list-staff"), eq(true));
        verify(model).addAttribute(eq("create-staff"), eq(true));
        verify(model, never()).addAttribute(eq("edit-staff"), eq(true));
        verify(model).addAttribute(eq("delete-staff"), eq(true));
        verify(model).addAttribute(eq("read-logs"), eq(true));

        assertEquals("dashboard", result);
    }

    @Test
    public void testDashboardNoDeleteStaffPermission() {
        when(principal.getName()).thenReturn("admin");
        when(principal.getAuthorities()).thenReturn(Arrays.asList(
                new SimpleGrantedAuthority(Permission.LIST_STAFF.name()),
                new SimpleGrantedAuthority(Permission.EDIT_STAFF.name()),
                new SimpleGrantedAuthority(Permission.CREATE_STAFF.name()),
                new SimpleGrantedAuthority(Permission.READ_LOGS.name())
        ));
        when(staffRepository.findByUsername(eq("admin"))).thenReturn(Optional.of(staff));

        String result = indexResource.dashboard(principal, model);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verify(model).addAttribute(eq("secure"), anyBoolean());
        verify(model).addAttribute(eq("staff"), eq(staff));
        verify(model).addAttribute(eq("list-staff"), eq(true));
        verify(model).addAttribute(eq("create-staff"), eq(true));
        verify(model).addAttribute(eq("edit-staff"), eq(true));
        verify(model, never()).addAttribute(eq("delete-staff"), eq(true));
        verify(model).addAttribute(eq("read-logs"), eq(true));

        assertEquals("dashboard", result);
    }

    @Test
    public void testDashboardNoReadLogPermission() {
        when(principal.getName()).thenReturn("admin");
        when(principal.getAuthorities()).thenReturn(Arrays.asList(
                new SimpleGrantedAuthority(Permission.LIST_STAFF.name()),
                new SimpleGrantedAuthority(Permission.EDIT_STAFF.name()),
                new SimpleGrantedAuthority(Permission.CREATE_STAFF.name()),
                new SimpleGrantedAuthority(Permission.DELETE_STAFF.name())
        ));
        when(staffRepository.findByUsername(eq("admin"))).thenReturn(Optional.of(staff));

        String result = indexResource.dashboard(principal, model);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verify(model).addAttribute(eq("secure"), anyBoolean());
        verify(model).addAttribute(eq("staff"), eq(staff));
        verify(model).addAttribute(eq("list-staff"), eq(true));
        verify(model).addAttribute(eq("create-staff"), eq(true));
        verify(model).addAttribute(eq("edit-staff"), eq(true));
        verify(model).addAttribute(eq("delete-staff"), eq(true));
        verify(model, never()).addAttribute(eq("read-logs"), eq(true));

        assertEquals("dashboard", result);
    }

    @Test
    public void testCreateStaff() {
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(
                new SimpleGrantedAuthority(Permission.CREATE_STAFF.name())
        ));

        String result = indexResource.createStaff(principal, model);

        assertEquals("staffcreate", result);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verify(model).addAttribute(eq("permissions"), any(Permission[].class));
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testCreateStaffNoPermission() {
        indexResource.createStaff(principal, model);
    }

    @Test
    public void testEditStaff() {
        UUID uuid = UUID.randomUUID();

        when(staffRepository.findById(eq(uuid))).thenReturn(Optional.of(staff));
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(
                new SimpleGrantedAuthority(Permission.EDIT_STAFF.name())
        ));

        String result = indexResource.editStaff(principal, model, uuid.toString());

        assertEquals("staffedit", result);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verify(model).addAttribute(eq("permissions"), any(Permission[].class));
        verify(model).addAttribute(eq("staff"), any(Staff.class));
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testEditStaffNoPermission() {
        indexResource.editStaff(principal, model, "staff_id");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEditStaffNoSuchId() {
        String id = "bogus_staff_id";

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(
                new SimpleGrantedAuthority(Permission.EDIT_STAFF.name())
        ));

        indexResource.editStaff(principal, model, id);
    }

    @Test
    public void testCreateAsset() {
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(
                new SimpleGrantedAuthority(Permission.CREATE_ASSET.name())
        ));

        String result = indexResource.createAsset(principal, model);

        assertEquals("assetcreate", result);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verify(model).addAttribute(eq("permissions"), any(Permission[].class));
        verify(model).addAttribute(eq("events"), anyCollection());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testCreateAssetNoPermission() {
        indexResource.createAsset(principal, model);
    }

    @Test
    public void testEditAsset() {
        UUID uuid = UUID.randomUUID();

        when(assetRepository.findById(eq(uuid))).thenReturn(Optional.of(asset));
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(
                new SimpleGrantedAuthority(Permission.EDIT_ASSET.name())
        ));

        String result = indexResource.editAsset(principal, model, uuid.toString());

        assertEquals("assetedit", result);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verify(model).addAttribute(eq("permissions"), any(Permission[].class));
        verify(model).addAttribute(eq("events"), anyCollection());
        verify(model).addAttribute(eq("asset"), any(Asset.class));
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testEditAssetNoPermission() {
        indexResource.editAsset(principal, model, "asset_id");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEditAssetNoSuchId() {
        String id = "bogus_asset_id";

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(
                new SimpleGrantedAuthority(Permission.EDIT_ASSET.name())
        ));

        indexResource.editAsset(principal, model, id);
    }

    @Test
    public void testCreateEvent() {
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(
                new SimpleGrantedAuthority(Permission.CREATE_EVENT.name())
        ));

        String result = indexResource.createEvent(principal, model);

        assertEquals("eventcreate", result);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verify(model).addAttribute(eq("permissions"), any(Permission[].class));
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testCreateEventNoPermission() {
        indexResource.createEvent(principal, model);
    }

    @Test
    public void testEditEvent() {
        UUID uuid = UUID.randomUUID();

        when(eventRepository.findById(eq(uuid))).thenReturn(Optional.of(event));
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(
                new SimpleGrantedAuthority(Permission.EDIT_EVENT.name())
        ));

        String result = indexResource.editEvent(principal, model, uuid.toString());

        assertEquals("eventedit", result);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verify(model).addAttribute(eq("permissions"), any(Permission[].class));
        verify(model).addAttribute(eq("event"), any(Event.class));
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testEditEventNoPermission() {
        indexResource.editEvent(principal, model, "event_id");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEditEventNoSuchId() {
        String id = "bogus_event_id";

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(
                new SimpleGrantedAuthority(Permission.EDIT_EVENT.name())
        ));

        indexResource.editEvent(principal, model, id);
    }

    @Test
    public void testCreateParticipant() {
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(
                new SimpleGrantedAuthority(Permission.CREATE_PARTICIPANT.name())
        ));

        String result = indexResource.createParticipant(principal, model);

        assertEquals("participantcreate", result);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verify(model).addAttribute(eq("permissions"), any(Permission[].class));
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testCreateParticipantNoPermission() {
        indexResource.createParticipant(principal, model);
    }

    @Test
    public void testEditParticipant() {
        UUID uuid = UUID.randomUUID();

        when(participantRepository.findById(eq(uuid))).thenReturn(Optional.of(participant));
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(
                new SimpleGrantedAuthority(Permission.EDIT_PARTICIPANT.name())
        ));

        String result = indexResource.editParticipant(principal, model, uuid.toString());

        assertEquals("participantedit", result);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verify(model).addAttribute(eq("permissions"), any(Permission[].class));
        verify(model).addAttribute(eq("participant"), any(Participant.class));
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testEditParticipantNoPermission() {
        indexResource.editParticipant(principal, model, "participant_id");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEditParticipantNoSuchId() {
        String id = "bogus_participant_id";

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(
                new SimpleGrantedAuthority(Permission.EDIT_PARTICIPANT.name())
        ));

        indexResource.editParticipant(principal, model, id);
    }
}
