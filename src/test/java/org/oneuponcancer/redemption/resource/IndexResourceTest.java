package org.oneuponcancer.redemption.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oneuponcancer.redemption.exception.InsufficientPermissionException;
import org.oneuponcancer.redemption.loader.StaffLoader;
import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.model.Staff;
import org.oneuponcancer.redemption.repository.AssetRepository;
import org.oneuponcancer.redemption.repository.StaffRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Collections;

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
    private Staff staff;

    private IndexResource indexResource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        indexResource = new IndexResource(APPLICATION_VERSION, staffLoader, staffRepository, assetRepository);
    }

    @Test
    public void testIndexNullPrincipal() throws Exception {
        String result = indexResource.index(null, model);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));

        assertEquals("index", result);
    }

    @Test
    public void testIndexPrincipal() throws Exception {
        String result = indexResource.index(principal, model);

        assertEquals("redirect:/dashboard", result);
    }

    @Test
    public void testLoginNullParams() throws Exception {
        String result = indexResource.login(null, null, model);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verifyNoMoreInteractions(model);

        assertEquals("index", result);
    }

    @Test
    public void testLoginLogout() throws Exception {
        String result = indexResource.login("logged out", null, model);

        verify(model).addAttribute(eq("message"), contains("logged out"));
        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verifyNoMoreInteractions(model);

        assertEquals("index", result);
    }

    @Test
    public void testLoginError() throws Exception {
        String result = indexResource.login(null, "credentials", model);

        verify(model).addAttribute(eq("message"), contains("credentials"));
        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verifyNoMoreInteractions(model);

        assertEquals("index", result);
    }

    @Test
    public void testDashboard() throws Exception {
        when(principal.getName()).thenReturn("admin");
        when(principal.getAuthorities()).thenReturn(Arrays.asList(
                new SimpleGrantedAuthority(Permission.LIST_STAFF.name()),
                new SimpleGrantedAuthority(Permission.EDIT_STAFF.name()),
                new SimpleGrantedAuthority(Permission.CREATE_STAFF.name()),
                new SimpleGrantedAuthority(Permission.DELETE_STAFF.name()),
                new SimpleGrantedAuthority(Permission.READ_LOGS.name())
        ));
        when(staffRepository.findByUsername(eq("admin"))).thenReturn(staff);

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
    public void testDashboardNoListStaffPermission() throws Exception {
        when(principal.getName()).thenReturn("admin");
        when(principal.getAuthorities()).thenReturn(Arrays.asList(
                new SimpleGrantedAuthority(Permission.EDIT_STAFF.name()),
                new SimpleGrantedAuthority(Permission.CREATE_STAFF.name()),
                new SimpleGrantedAuthority(Permission.DELETE_STAFF.name()),
                new SimpleGrantedAuthority(Permission.READ_LOGS.name())
        ));
        when(staffRepository.findByUsername(eq("admin"))).thenReturn(staff);

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
    public void testDashboardNoCreateStaffPermission() throws Exception {
        when(principal.getName()).thenReturn("admin");
        when(principal.getAuthorities()).thenReturn(Arrays.asList(
                new SimpleGrantedAuthority(Permission.LIST_STAFF.name()),
                new SimpleGrantedAuthority(Permission.EDIT_STAFF.name()),
                new SimpleGrantedAuthority(Permission.DELETE_STAFF.name()),
                new SimpleGrantedAuthority(Permission.READ_LOGS.name())
        ));
        when(staffRepository.findByUsername(eq("admin"))).thenReturn(staff);

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
    public void testDashboardNoEditStaffPermission() throws Exception {
        when(principal.getName()).thenReturn("admin");
        when(principal.getAuthorities()).thenReturn(Arrays.asList(
                new SimpleGrantedAuthority(Permission.LIST_STAFF.name()),
                new SimpleGrantedAuthority(Permission.CREATE_STAFF.name()),
                new SimpleGrantedAuthority(Permission.DELETE_STAFF.name()),
                new SimpleGrantedAuthority(Permission.READ_LOGS.name())
        ));
        when(staffRepository.findByUsername(eq("admin"))).thenReturn(staff);

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
    public void testDashboardNoDeleteStaffPermission() throws Exception {
        when(principal.getName()).thenReturn("admin");
        when(principal.getAuthorities()).thenReturn(Arrays.asList(
                new SimpleGrantedAuthority(Permission.LIST_STAFF.name()),
                new SimpleGrantedAuthority(Permission.EDIT_STAFF.name()),
                new SimpleGrantedAuthority(Permission.CREATE_STAFF.name()),
                new SimpleGrantedAuthority(Permission.READ_LOGS.name())
        ));
        when(staffRepository.findByUsername(eq("admin"))).thenReturn(staff);

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
    public void testDashboardNoReadLogPermission() throws Exception {
        when(principal.getName()).thenReturn("admin");
        when(principal.getAuthorities()).thenReturn(Arrays.asList(
                new SimpleGrantedAuthority(Permission.LIST_STAFF.name()),
                new SimpleGrantedAuthority(Permission.EDIT_STAFF.name()),
                new SimpleGrantedAuthority(Permission.CREATE_STAFF.name()),
                new SimpleGrantedAuthority(Permission.DELETE_STAFF.name())
        ));
        when(staffRepository.findByUsername(eq("admin"))).thenReturn(staff);

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
    public void testCreateStaff() throws Exception {
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(
                new SimpleGrantedAuthority(Permission.CREATE_STAFF.name())
        ));

        String result = indexResource.createStaff(principal, model);

        assertEquals("staffcreate", result);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verify(model).addAttribute(eq("permissions"), anyCollectionOf(Permission.class));
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testCreateStaffNoPermission() throws Exception {
        indexResource.createStaff(principal, model);
    }

    @Test
    public void testEditStaff() throws Exception {
        String id = "staff_id";

        when(staffRepository.findOne(eq(id))).thenReturn(staff);
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(
                new SimpleGrantedAuthority(Permission.EDIT_STAFF.name())
        ));

        String result = indexResource.editStaff(principal, model, id);

        assertEquals("staffedit", result);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verify(model).addAttribute(eq("permissions"), anyCollectionOf(Permission.class));
        verify(model).addAttribute(eq("staff"), any(Staff.class));
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testEditStaffNoPermission() throws Exception {
        indexResource.editStaff(principal, model, "staff_id");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEditStaffNoSuchId() throws Exception {
        String id = "bogus_staff_id";

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(
                new SimpleGrantedAuthority(Permission.EDIT_STAFF.name())
        ));

        indexResource.editStaff(principal, model, id);
    }
}
