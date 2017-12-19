package org.oneuponcancer.redemption.model.transport;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.oneuponcancer.redemption.model.Permission;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class StaffEditRequestTest {
    private StaffEditRequest staffEditRequest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        staffEditRequest = new StaffEditRequest();
    }

    @Test
    public void testUsername() throws Exception {
        staffEditRequest.setUsername("jimmy");

        assertEquals("jimmy", staffEditRequest.getUsername());
    }

    @Test
    public void testPassword() throws Exception {
        staffEditRequest.setPassword("secret");

        assertEquals("secret", staffEditRequest.getPassword());
    }

    @Test
    public void testPermissions() throws Exception {
        List<String> permissions = Arrays.stream(Permission.values())
                .map(Permission::getUnique)
                .collect(Collectors.toList());

        staffEditRequest.setPermissions(permissions);

        assertEquals(permissions, staffEditRequest.getPermissions());
    }
}
