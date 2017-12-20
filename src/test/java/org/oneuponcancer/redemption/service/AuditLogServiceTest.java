package org.oneuponcancer.redemption.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oneuponcancer.redemption.model.AuditLog;
import org.oneuponcancer.redemption.repository.AuditLogRepository;
import org.oneuponcancer.redemption.wrapper.RequestContextHolderWrapper;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AuditLogServiceTest {
    @Captor
    private ArgumentCaptor<AuditLog> auditLogArgumentCaptor;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private RequestContextHolderWrapper requestContextHolderWrapper;

    private AuditLogService auditLogService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        auditLogService = new AuditLogService(auditLogRepository, requestContextHolderWrapper);
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

    @Test
    public void testOnAuthenticationSuccessEventNoForward() throws Exception {
        User user = new User("username", "password", Collections.singletonList(new SimpleGrantedAuthority("USER")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "password");
        AuthenticationSuccessEvent event = new AuthenticationSuccessEvent(authentication);
        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);

        when(request.getRemoteAddr()).thenReturn("10.0.0.111");
        when(requestContextHolderWrapper.getRequestAttributes()).thenReturn(requestAttributes);

        auditLogService.onApplicationEvent(event);

        verify(auditLogRepository).save(auditLogArgumentCaptor.capture());

        AuditLog auditLog = auditLogArgumentCaptor.getValue();

        assertNotNull(auditLog.getTimestamp());
        assertEquals("username", auditLog.getUsername());
        assertEquals("10.0.0.111", auditLog.getRemoteAddress());
        assertNotNull(auditLog.getMessage());
    }

    @Test
    public void testOnAuthenticationSuccessEventWithForward() throws Exception {
        User user = new User("username", "password", Collections.singletonList(new SimpleGrantedAuthority("USER")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "password");
        AuthenticationSuccessEvent event = new AuthenticationSuccessEvent(authentication);
        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);

        when(request.getRemoteAddr()).thenReturn("10.0.0.111");
        when(request.getHeader(eq("x-forwarded-for"))).thenReturn("172.20.0.5,100.99.98.97");
        when(requestContextHolderWrapper.getRequestAttributes()).thenReturn(requestAttributes);

        auditLogService.onApplicationEvent(event);

        verify(auditLogRepository).save(auditLogArgumentCaptor.capture());

        AuditLog auditLog = auditLogArgumentCaptor.getValue();

        assertNotNull(auditLog.getTimestamp());
        assertEquals("username", auditLog.getUsername());
        assertEquals("100.99.98.97", auditLog.getRemoteAddress());
        assertNotNull(auditLog.getMessage());
    }

    @Test
    public void testOnAuthenticationFailureNoForward() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken("username", "password");
        AuthenticationException ex = new AuthenticationCredentialsNotFoundException("Credentials not found.");
        AuthenticationFailureBadCredentialsEvent event = new AuthenticationFailureBadCredentialsEvent(authentication, ex);
        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);

        when(request.getRemoteAddr()).thenReturn("10.0.0.111");
        when(requestContextHolderWrapper.getRequestAttributes()).thenReturn(requestAttributes);

        auditLogService.onApplicationEvent(event);

        verify(auditLogRepository).save(auditLogArgumentCaptor.capture());

        AuditLog auditLog = auditLogArgumentCaptor.getValue();

        assertNotNull(auditLog.getTimestamp());
        assertEquals("username", auditLog.getUsername());
        assertEquals("10.0.0.111", auditLog.getRemoteAddress());
        assertNotNull(auditLog.getMessage());
    }

    @Test
    public void testOnAuthenticationFailureWithForward() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken("username", "password");
        AuthenticationException ex = new AuthenticationCredentialsNotFoundException("Credentials not found.");
        AuthenticationFailureBadCredentialsEvent event = new AuthenticationFailureBadCredentialsEvent(authentication, ex);
        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);

        when(request.getRemoteAddr()).thenReturn("10.0.0.111");
        when(request.getHeader(eq("x-forwarded-for"))).thenReturn("172.20.0.5,100.99.98.97");
        when(requestContextHolderWrapper.getRequestAttributes()).thenReturn(requestAttributes);

        auditLogService.onApplicationEvent(event);

        verify(auditLogRepository).save(auditLogArgumentCaptor.capture());

        AuditLog auditLog = auditLogArgumentCaptor.getValue();

        assertNotNull(auditLog.getTimestamp());
        assertEquals("username", auditLog.getUsername());
        assertEquals("100.99.98.97", auditLog.getRemoteAddress());
        assertNotNull(auditLog.getMessage());
    }
}
