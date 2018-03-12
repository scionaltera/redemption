package org.oneuponcancer.redemption.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oneuponcancer.redemption.exception.InsufficientPermissionException;
import org.oneuponcancer.redemption.loader.StaffLoader;
import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.model.Staff;
import org.oneuponcancer.redemption.model.transport.StaffCreateRequest;
import org.oneuponcancer.redemption.model.transport.StaffEditRequest;
import org.oneuponcancer.redemption.repository.StaffRepository;
import org.oneuponcancer.redemption.service.AuditLogService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class StaffResourceTest {
    @Captor
    private ArgumentCaptor<Staff> staffArgumentCaptor;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private StaffLoader staffLoader;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private UsernamePasswordAuthenticationToken principal;

    @Mock
    private HttpServletRequest request;

    private List<Staff> allStaff = new ArrayList<>();

    private StaffResource staffResource;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        for (int i = 0; i < 3; i++) {
            allStaff.add(mock(Staff.class));
        }

        when(staffRepository.findAll()).thenReturn(allStaff);
        when(staffRepository.save(any(Staff.class))).thenAnswer(i -> {
            Staff staff = i.getArgumentAt(0, Staff.class);

            staff.setId(UUID.randomUUID());

            return staff;
        });

        staffResource = new StaffResource(
                staffRepository,
                staffLoader,
                auditLogService,
                bCryptPasswordEncoder);
    }

    @Test
    public void testFetchStaff() {
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.LIST_STAFF.name())));

        List<Staff> result = staffResource.fetchStaff(principal);

        assertFalse(result.isEmpty());

        result.forEach(s -> verify(s).setPassword(eq("********")));
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testFetchStaffNoPermission() {
        staffResource.fetchStaff(principal);
    }

    @Test
    public void testCreateStaff() {
        StaffCreateRequest createRequest = mock(StaffCreateRequest.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.CREATE_STAFF.name())));
        when(bCryptPasswordEncoder.encode(eq("secret"))).thenReturn("encrypted");
        when(createRequest.getUsername()).thenReturn("biff");
        when(createRequest.getPassword()).thenReturn("secret");
        when(createRequest.getPermissions()).thenReturn(Arrays.asList(
                Permission.LOGIN.getUnique(),
                Permission.READ_LOGS.getUnique()
        ));

        Staff response = staffResource.createStaff(
                createRequest,
                bindingResult,
                principal,
                request
        );

        assertNotNull(response);
        verify(bCryptPasswordEncoder).encode(eq("secret"));
        verify(staffRepository).save(staffArgumentCaptor.capture());
        verify(staffLoader).evaluateSecurity();
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(anyString(), anyString(), anyString());

        Staff staff = staffArgumentCaptor.getValue();

        assertEquals("biff", staff.getUsername());
        assertEquals("encrypted", staff.getPassword());
        assertFalse(staff.getPermissions().isEmpty());
        assertNotNull(staff.getId());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testCreateStaffNoPermission() {
        StaffCreateRequest createRequest = mock(StaffCreateRequest.class);

        when(createRequest.getUsername()).thenReturn("biff");
        when(createRequest.getPassword()).thenReturn("secret");
        when(createRequest.getPermissions()).thenReturn(Arrays.asList(
                Permission.LOGIN.getUnique(),
                Permission.READ_LOGS.getUnique()
        ));

        staffResource.createStaff(
                createRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testCreateStaffInvalidUsername() {
        StaffCreateRequest createRequest = mock(StaffCreateRequest.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.CREATE_STAFF.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid username.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(createRequest.getUsername()).thenReturn("");
        when(createRequest.getPassword()).thenReturn("secret");
        when(createRequest.getPermissions()).thenReturn(Arrays.asList(
                Permission.LOGIN.getUnique(),
                Permission.READ_LOGS.getUnique()
        ));

        staffResource.createStaff(
                createRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testCreateStaffInvalidPassword() {
        StaffCreateRequest createRequest = mock(StaffCreateRequest.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.CREATE_STAFF.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid password.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(createRequest.getUsername()).thenReturn("biff");
        when(createRequest.getPassword()).thenReturn("");
        when(createRequest.getPermissions()).thenReturn(Arrays.asList(
                Permission.LOGIN.getUnique(),
                Permission.READ_LOGS.getUnique()
        ));

        staffResource.createStaff(
                createRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test
    public void testUpdateStaff() {
        UUID uuid = UUID.randomUUID();
        StaffEditRequest editRequest = mock(StaffEditRequest.class);
        Staff staff = mock(Staff.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_STAFF.name())));
        when(bCryptPasswordEncoder.encode(eq("secret"))).thenReturn("encrypted");
        when(staffRepository.findOne(eq(uuid))).thenReturn(staff);
        when(editRequest.getUsername()).thenReturn("admin");
        when(editRequest.getPassword()).thenReturn("secret");
        when(editRequest.getPermissions()).thenReturn(Arrays.asList(
                Permission.LOGIN.getUnique(),
                Permission.READ_LOGS.getUnique()
        ));

        Staff response = staffResource.updateStaff(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );

        assertNotNull(response);
        verify(staff).setUsername(eq("admin"));
        verify(staff).setPassword(eq("encrypted"));
        verify(staff, times(Permission.values().length)).removePermission(any(Permission.class));
        verify(staff, atLeastOnce()).addPermission(any(Permission.class));
        verify(bCryptPasswordEncoder).encode(eq("secret"));
        verify(staffRepository).save(eq(staff));
        verify(staffLoader).evaluateSecurity();
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(anyString(), anyString(), anyString());
    }

    @Test
    public void testUpdateStaffNoPasswordChange() {
        UUID uuid = UUID.randomUUID();
        StaffEditRequest editRequest = mock(StaffEditRequest.class);
        Staff staff = mock(Staff.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_STAFF.name())));
        when(staffRepository.findOne(eq(uuid))).thenReturn(staff);
        when(editRequest.getUsername()).thenReturn("admin");
        when(editRequest.getPermissions()).thenReturn(Arrays.asList(
                Permission.LOGIN.getUnique(),
                Permission.READ_LOGS.getUnique()
        ));

        Staff response = staffResource.updateStaff(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );

        assertNotNull(response);
        verify(staff).setUsername(eq("admin"));
        verify(staff, never()).setPassword(anyString());
        verify(staff, times(Permission.values().length)).removePermission(any(Permission.class));
        verify(staff, atLeastOnce()).addPermission(any(Permission.class));
        verify(bCryptPasswordEncoder, never()).encode(anyString());
        verify(staffRepository).save(eq(staff));
        verify(staffLoader).evaluateSecurity();
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(anyString(), anyString(), anyString());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testUpdateStaffNoPermission() {
        UUID uuid = UUID.randomUUID();
        StaffEditRequest editRequest = mock(StaffEditRequest.class);
        Staff staff = mock(Staff.class);

        when(staffRepository.findOne(eq(uuid))).thenReturn(staff);
        when(editRequest.getUsername()).thenReturn("admin");
        when(editRequest.getPassword()).thenReturn("secret");
        when(editRequest.getPermissions()).thenReturn(Arrays.asList(
                Permission.LOGIN.getUnique(),
                Permission.READ_LOGS.getUnique()
        ));

        staffResource.updateStaff(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateStaffNotFound() {
        UUID uuid = UUID.randomUUID();
        StaffEditRequest editRequest = mock(StaffEditRequest.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_STAFF.name())));
        when(editRequest.getUsername()).thenReturn("admin");
        when(editRequest.getPassword()).thenReturn("secret");
        when(editRequest.getPermissions()).thenReturn(Arrays.asList(
                Permission.LOGIN.getUnique(),
                Permission.READ_LOGS.getUnique()
        ));

        staffResource.updateStaff(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testUpdateStaffBadUsername() {
        UUID uuid = UUID.randomUUID();
        StaffEditRequest editRequest = mock(StaffEditRequest.class);
        Staff staff = mock(Staff.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_STAFF.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid username.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(staffRepository.findOne(eq(uuid))).thenReturn(staff);
        when(editRequest.getUsername()).thenReturn("");
        when(editRequest.getPassword()).thenReturn("secret");
        when(editRequest.getPermissions()).thenReturn(Arrays.asList(
                Permission.LOGIN.getUnique(),
                Permission.READ_LOGS.getUnique()
        ));

        staffResource.updateStaff(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testUpdateStaffBadPassword() {
        UUID uuid = UUID.randomUUID();
        StaffEditRequest editRequest = mock(StaffEditRequest.class);
        Staff staff = mock(Staff.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_STAFF.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid password.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(staffRepository.findOne(eq(uuid))).thenReturn(staff);
        when(editRequest.getUsername()).thenReturn("admin");
        when(editRequest.getPassword()).thenReturn("");
        when(editRequest.getPermissions()).thenReturn(Arrays.asList(
                Permission.LOGIN.getUnique(),
                Permission.READ_LOGS.getUnique()
        ));

        staffResource.updateStaff(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test
    public void testDeleteStaff() {
        UUID uuid = UUID.randomUUID();
        Staff staff = mock(Staff.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.DELETE_STAFF.name())));
        when(staffRepository.findOne(eq(uuid))).thenReturn(staff);

        Staff result = staffResource.deleteStaff(
                uuid.toString(),
                principal,
                request
        );

        assertEquals(staff, result);
        verify(staffRepository).findOne(eq(uuid));
        verify(staffRepository).delete(eq(staff));
        verify(staffLoader).evaluateSecurity();
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(anyString(), anyString(), anyString());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testDeleteStaffNoPermission() {
        UUID uuid = UUID.randomUUID();

        staffResource.deleteStaff(
                uuid.toString(),
                principal,
                request
        );
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteStaffNotFound() {
        UUID uuid = UUID.randomUUID();

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.DELETE_STAFF.name())));

        staffResource.deleteStaff(
                uuid.toString(),
                principal,
                request
        );
    }
}
