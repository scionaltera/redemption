package org.oneuponcancer.redemption.service;

import org.apache.commons.text.StringEscapeUtils;
import org.oneuponcancer.redemption.model.AuditLog;
import org.oneuponcancer.redemption.repository.AuditLogRepository;
import org.oneuponcancer.redemption.wrapper.RequestContextHolderWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class AuditLogService implements ApplicationListener<AbstractAuthenticationEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogService.class);

    private AuditLogRepository auditLogRepository;
    private RequestContextHolderWrapper requestContextHolderWrapper;

    @Inject
    public AuditLogService(AuditLogRepository auditLogRepository, RequestContextHolderWrapper requestContextHolderWrapper) {
        this.auditLogRepository = auditLogRepository;
        this.requestContextHolderWrapper = requestContextHolderWrapper;
    }

    public void log(String username, String remoteAddress, String message) {
        if (message == null || StringUtils.isEmpty(message.trim())) {
            throw new IllegalArgumentException("Message may not be empty.");
        }

        AuditLog entry = new AuditLog();

        entry.setTimestamp(System.currentTimeMillis());
        entry.setUsername(StringEscapeUtils.escapeHtml4(username));
        entry.setRemoteAddress(remoteAddress);
        entry.setMessage(StringEscapeUtils.escapeHtml4(message));

        auditLogRepository.save(entry);

        LOGGER.info("Audit log: {}", message);
    }

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationSuccessEvent) {
            onApplicationEvent((AuthenticationSuccessEvent) event);
        } else if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            onApplicationEvent((AuthenticationFailureBadCredentialsEvent)event);
        }
    }

    private void onApplicationEvent(AuthenticationSuccessEvent event) {
        User principal = (User)event.getAuthentication().getPrincipal();
        String username = principal.getUsername();
        HttpServletRequest request = ((ServletRequestAttributes) requestContextHolderWrapper.getRequestAttributes()).getRequest();
        String remoteAddress = extractRemoteIp(request);

        log(username, remoteAddress, "Successful authentication.");
    }

    private void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        UsernamePasswordAuthenticationToken principal = (UsernamePasswordAuthenticationToken)event.getAuthentication();
        String username = (String)principal.getPrincipal();
        HttpServletRequest request = ((ServletRequestAttributes) requestContextHolderWrapper.getRequestAttributes()).getRequest();
        String remoteAddress = extractRemoteIp(request);

        log(username, remoteAddress, "Failed authentication attempt.");
    }

    private String extractRemoteIp(HttpServletRequest request) {
        String forwardedHeader = request.getHeader("x-forwarded-for");

        if (forwardedHeader != null) {
            String[] addresses = forwardedHeader.split("[,]");

            for (String address : addresses) {
                try {
                    InetAddress inetAddress = InetAddress.getByName(address);

                    if (!inetAddress.isSiteLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                } catch (UnknownHostException e) {
                    LOGGER.debug("Failed to resolve IP for address: {}", address);
                }
            }
        }

        return request.getRemoteAddr();
    }
}
