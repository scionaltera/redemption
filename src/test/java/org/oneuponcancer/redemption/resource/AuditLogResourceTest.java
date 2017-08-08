package org.oneuponcancer.redemption.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oneuponcancer.redemption.model.AuditLog;
import org.oneuponcancer.redemption.repository.AuditLogRepository;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AuditLogResourceTest {
    private static final int REQUEST_MAX = 10;

    @Mock
    private AuditLogRepository auditLogRepository;

    private AuditLogResource auditLogResource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(auditLogRepository.findAll(any(PageRequest.class))).thenAnswer(invocation -> {
            PageRequest request = invocation.getArgumentAt(0, PageRequest.class);
            List<AuditLog> items = new ArrayList<>();

            for (int i = 0; i < request.getPageSize(); i++) {
                AuditLog log = mock(AuditLog.class);

                when(log.getTimestamp()).thenReturn(System.currentTimeMillis());
                when(log.getUsername()).thenReturn("user" + i);
                when(log.getRemoteAddress()).thenReturn("1.2.3." + i);
                when(log.getMessage()).thenReturn("Fake!");

                items.add(log);
            }

            return new PageImpl<>(items);
        });

        auditLogResource = new AuditLogResource(REQUEST_MAX, auditLogRepository);
    }

    @Test
    public void testFetch() throws Exception {
        List<AuditLog> results = auditLogResource.fetchAuditLogs(null);

        assertEquals(REQUEST_MAX, results.size());
    }

    @Test
    public void testFetchLimited() throws Exception {
        List<AuditLog> results = auditLogResource.fetchAuditLogs(3);

        assertEquals(3, results.size());
    }

    @Test
    public void testFetchNegative() throws Exception {
        List<AuditLog> results = auditLogResource.fetchAuditLogs(-1);

        assertEquals(1, results.size());
    }

    @Test
    public void testFetchTooMany() throws Exception {
        List<AuditLog> results = auditLogResource.fetchAuditLogs(REQUEST_MAX + 1);

        assertEquals(REQUEST_MAX, results.size());
    }
}
