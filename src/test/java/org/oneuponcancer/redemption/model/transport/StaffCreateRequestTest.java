package org.oneuponcancer.redemption.model.transport;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.oneuponcancer.redemption.model.Permission;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class StaffCreateRequestTest {
    private StaffCreateRequest staffCreateRequest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        staffCreateRequest = new StaffCreateRequest();
    }

    @Test
    public void testUsername() throws Exception {
        staffCreateRequest.setUsername("jimmy");

        assertEquals("jimmy", staffCreateRequest.getUsername());
    }

    @Test
    public void testPassword() throws Exception {
        staffCreateRequest.setPassword("secret");

        assertEquals("secret", staffCreateRequest.getPassword());
    }

    @Test
    public void testPermissions() throws Exception {
        List<String> permissions = Arrays.stream(Permission.values())
                .map(Permission::getUnique)
                .collect(Collectors.toList());

        staffCreateRequest.setPermissions(permissions);

        assertEquals(permissions, staffCreateRequest.getPermissions());
    }
}
