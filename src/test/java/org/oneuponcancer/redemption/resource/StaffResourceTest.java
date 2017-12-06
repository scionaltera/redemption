package org.oneuponcancer.redemption.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oneuponcancer.redemption.exception.InsufficientPermissionException;
import org.oneuponcancer.redemption.loader.StaffLoader;
import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.model.Staff;
import org.oneuponcancer.redemption.model.transport.StaffEditRequest;
import org.oneuponcancer.redemption.repository.StaffRepository;
import org.oneuponcancer.redemption.service.AuditLogService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class StaffResourceTest {
    @Mock
    private StaffRepository staffRepository;

    @Mock
    private StaffLoader staffLoader;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private UsernamePasswordAuthenticationToken principal;

    @Mock
    private HttpServletRequest request;

    private List<Staff> allStaff = new ArrayList<>();

    private StaffResource staffResource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        for (int i = 0; i < 3; i++) {
            allStaff.add(mock(Staff.class));
        }

        when(staffRepository.findAll()).thenReturn(allStaff);

        staffResource = new StaffResource(
                staffRepository,
                staffLoader,
                auditLogService,
                bCryptPasswordEncoder);
    }

    @Test
    public void testFetchStaff() throws Exception {
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.LIST_STAFF.name())));

        List<Staff> result = staffResource.fetchStaff(principal);

        assertFalse(result.isEmpty());

        result.forEach(s -> verify(s).setPassword(eq("********")));
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testFetchStaffNoPermission() throws Exception {
        staffResource.fetchStaff(principal);
    }

    @Test
    public void testUpdateStaff() throws Exception {
        String id = "1";
        StaffEditRequest editRequest = mock(StaffEditRequest.class);
        Staff staff = mock(Staff.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_STAFF.name())));
        when(staffRepository.findOne(eq(id))).thenReturn(staff);
        when(editRequest.getUsername()).thenReturn("admin");
        when(editRequest.getPassword()).thenReturn("secret");
        when(editRequest.getPermissions()).thenReturn(Arrays.asList(
                Permission.LOGIN.getUnique(),
                Permission.READ_LOGS.getUnique()
        ));

        String response = staffResource.updateStaff(
                id,
                editRequest,
                principal,
                request
        );

        assertEquals("redirect:/dashboard", response);
        verify(staff, times(Permission.values().length)).removePermission(any(Permission.class));
        verify(staff, atLeastOnce()).addPermission(any(Permission.class));
        verify(bCryptPasswordEncoder).encode(anyString());
        verify(staffRepository).save(eq(staff));
        verify(staffLoader).evaluateSecurity();
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(anyString(), anyString(), anyString());
    }

    @Test
    public void testUpdateStaffNoPasswordChange() throws Exception {
        String id = "1";
        StaffEditRequest editRequest = mock(StaffEditRequest.class);
        Staff staff = mock(Staff.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_STAFF.name())));
        when(staffRepository.findOne(eq(id))).thenReturn(staff);
        when(editRequest.getUsername()).thenReturn("admin");
        when(editRequest.getPermissions()).thenReturn(Arrays.asList(
                Permission.LOGIN.getUnique(),
                Permission.READ_LOGS.getUnique()
        ));

        String response = staffResource.updateStaff(
                id,
                editRequest,
                principal,
                request
        );

        assertEquals("redirect:/dashboard", response);
        verify(staff, times(Permission.values().length)).removePermission(any(Permission.class));
        verify(staff, atLeastOnce()).addPermission(any(Permission.class));
        verify(bCryptPasswordEncoder, never()).encode(anyString());
        verify(staffRepository).save(eq(staff));
        verify(staffLoader).evaluateSecurity();
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(anyString(), anyString(), anyString());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testUpdateStaffNoPermission() throws Exception {
        String id = "1";
        StaffEditRequest editRequest = mock(StaffEditRequest.class);
        Staff staff = mock(Staff.class);

        when(staffRepository.findOne(eq(id))).thenReturn(staff);
        when(editRequest.getUsername()).thenReturn("admin");
        when(editRequest.getPassword()).thenReturn("secret");
        when(editRequest.getPermissions()).thenReturn(Arrays.asList(
                Permission.LOGIN.getUnique(),
                Permission.READ_LOGS.getUnique()
        ));

        staffResource.updateStaff(
                id,
                editRequest,
                principal,
                request
        );
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateStaffNotFound() throws Exception {
        String id = "1";
        StaffEditRequest editRequest = mock(StaffEditRequest.class);
        Staff staff = mock(Staff.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_STAFF.name())));
        when(editRequest.getUsername()).thenReturn("admin");
        when(editRequest.getPassword()).thenReturn("secret");
        when(editRequest.getPermissions()).thenReturn(Arrays.asList(
                Permission.LOGIN.getUnique(),
                Permission.READ_LOGS.getUnique()
        ));

        staffResource.updateStaff(
                id,
                editRequest,
                principal,
                request
        );
    }
}
