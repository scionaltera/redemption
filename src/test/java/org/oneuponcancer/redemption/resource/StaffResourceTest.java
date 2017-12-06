package org.oneuponcancer.redemption.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oneuponcancer.redemption.exception.InsufficientPermissionException;
import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.model.Staff;
import org.oneuponcancer.redemption.repository.StaffRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StaffResourceTest {
    @Mock
    private StaffRepository staffRepository;

    @Mock
    private UsernamePasswordAuthenticationToken principal;

    private List<Staff> allStaff = new ArrayList<>();

    private StaffResource staffResource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        for (int i = 0; i < 3; i++) {
            allStaff.add(mock(Staff.class));
        }

        when(staffRepository.findAll()).thenReturn(allStaff);

        staffResource = new StaffResource(staffRepository);
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
}
