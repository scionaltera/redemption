package org.oneuponcancer.redemption.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oneuponcancer.redemption.loader.StaffLoader;
import org.oneuponcancer.redemption.model.Staff;
import org.oneuponcancer.redemption.repository.StaffRepository;
import org.springframework.ui.Model;

import java.security.Principal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class IndexResourceTest {
    private static final String APPLICATION_VERSION = "0.0.0";

    @Mock
    private Principal principal;

    @Mock
    private Model model;

    @Mock
    private StaffLoader staffLoader;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private Staff staff;

    private IndexResource indexResource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        indexResource = new IndexResource(APPLICATION_VERSION, staffLoader, staffRepository);
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
        when(staffRepository.findByUsername(eq("admin"))).thenReturn(staff);

        String result = indexResource.dashboard(principal, model);

        verify(model).addAttribute(eq("version"), eq(APPLICATION_VERSION));
        verify(model).addAttribute(eq("secure"), anyBoolean());
        verify(model).addAttribute(eq("staff"), eq(staff));

        assertEquals("dashboard", result);
    }
}
