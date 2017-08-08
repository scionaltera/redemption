package org.oneuponcancer.redemption.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oneuponcancer.redemption.model.AuditLog;
import org.oneuponcancer.redemption.repository.AuditLogRepository;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AuditLogServiceTest {
    @Captor
    private ArgumentCaptor<AuditLog> auditLogArgumentCaptor;

    @Mock
    private AuditLogRepository auditLogRepository;

    private AuditLogService auditLogService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        auditLogService = new AuditLogService(auditLogRepository);
    }

    @Test
    public void testLog() throws Exception {
        auditLogService.log("user", "1.2.3.4", "Foo.");

        verify(auditLogRepository).save(auditLogArgumentCaptor.capture());

        AuditLog auditLog = auditLogArgumentCaptor.getValue();

        assertNotNull(auditLog.getTimestamp());
        assertEquals("user", auditLog.getUsername());
        assertEquals("1.2.3.4", auditLog.getRemoteAddress());
        assertEquals("Foo.", auditLog.getMessage());
    }

    @Test
    public void testLogHtmlEscape() throws Exception {
        auditLogService.log("<blink>user</blink>", "1.2.3.4", "<p>Foo.</p>");

        verify(auditLogRepository).save(auditLogArgumentCaptor.capture());

        AuditLog auditLog = auditLogArgumentCaptor.getValue();

        assertNotNull(auditLog.getTimestamp());
        assertEquals("&lt;blink&gt;user&lt;/blink&gt;", auditLog.getUsername());
        assertEquals("1.2.3.4", auditLog.getRemoteAddress());
        assertEquals("&lt;p&gt;Foo.&lt;/p&gt;", auditLog.getMessage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLogEmptyMessage1() throws Exception {
        auditLogService.log("user", "1.2.3.4", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLogEmptyMessage2() throws Exception {
        auditLogService.log("user", "1.2.3.4", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLogEmptyMessage3() throws Exception {
        auditLogService.log("user", "1.2.3.4", "  ");
    }

    @Test
    public void testLogEmptyUserAndRemote() throws Exception {
        auditLogService.log(null, null, "Foo.");

        verify(auditLogRepository).save(auditLogArgumentCaptor.capture());

        AuditLog auditLog = auditLogArgumentCaptor.getValue();

        assertNotNull(auditLog.getTimestamp());
        assertNull(auditLog.getUsername());
        assertNull(auditLog.getRemoteAddress());
        assertEquals("Foo.", auditLog.getMessage());
    }
}
