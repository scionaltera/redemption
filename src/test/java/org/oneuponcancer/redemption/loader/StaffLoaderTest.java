package org.oneuponcancer.redemption.loader;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.model.Staff;
import org.oneuponcancer.redemption.repository.StaffRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StaffLoaderTest {
    @Captor
    private ArgumentCaptor<Staff> staffArgumentCaptor;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private StaffLoader staffLoader;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(bCryptPasswordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgumentAt(0, String.class));
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenAnswer(
                invocation -> invocation.getArgumentAt(0, String.class).equals(invocation.getArgumentAt(1, String.class)));

        staffLoader = new StaffLoader(staffRepository, bCryptPasswordEncoder);
    }

    @Test
    public void testLoadEmptyDatabase() throws Exception {
        when(staffRepository.count()).thenReturn(0L);

        staffLoader.load();

        verify(staffRepository).save(staffArgumentCaptor.capture());
        verify(bCryptPasswordEncoder).encode(eq(StaffLoader.DEFAULT_PASS));

        Staff staff = staffArgumentCaptor.getValue();

        assertEquals(StaffLoader.DEFAULT_USER, staff.getUsername());
        assertEquals(StaffLoader.DEFAULT_PASS, staff.getPassword());

        Arrays.stream(Permission.values()).forEach(p -> assertTrue(staff.hasPermission(Permission.LOGIN)));

        assertFalse(staffLoader.isSecure());
    }

    @Test
    public void testLoadDatabaseWithDefaultUser() throws Exception {
        Staff user = new Staff();

        user.setUsername(StaffLoader.DEFAULT_USER);
        user.setPassword(StaffLoader.DEFAULT_PASS);

        when(staffRepository.count()).thenReturn(1L);
        when(staffRepository.findByUsername(eq(StaffLoader.DEFAULT_USER))).thenReturn(user);

        staffLoader.load();

        verify(bCryptPasswordEncoder).matches(eq(StaffLoader.DEFAULT_PASS), eq(user.getPassword()));

        assertFalse(staffLoader.isSecure());
    }

    @Test
    public void testLoadDatabaseNoDefaultUser() throws Exception {
        Staff user = new Staff();

        user.setUsername(StaffLoader.DEFAULT_USER);
        user.setPassword("secure");

        when(staffRepository.count()).thenReturn(1L);
        when(staffRepository.findByUsername(eq(StaffLoader.DEFAULT_USER))).thenReturn(user);

        staffLoader.load();

        verify(bCryptPasswordEncoder).matches(eq(StaffLoader.DEFAULT_PASS), eq(user.getPassword()));

        assertTrue(staffLoader.isSecure());
    }
}
