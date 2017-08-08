package org.oneuponcancer.redemption.service;

import org.apache.commons.text.StringEscapeUtils;
import org.oneuponcancer.redemption.model.AuditLog;
import org.oneuponcancer.redemption.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.inject.Inject;

@Component
public class AuditLogService implements ApplicationListener<AbstractAuthenticationEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogService.class);

    private AuditLogRepository auditLogRepository;

    @Inject
    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
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
        String remoteAddress = ((WebAuthenticationDetails)((UsernamePasswordAuthenticationToken)event.getSource()).getDetails()).getRemoteAddress();

        log(username, remoteAddress, "Successful authentication.");
    }

    private void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        UsernamePasswordAuthenticationToken principal = (UsernamePasswordAuthenticationToken)event.getAuthentication();
        String username = (String)principal.getPrincipal();
        String remoteAddress = ((WebAuthenticationDetails)principal.getDetails()).getRemoteAddress();

        log(username, remoteAddress, "Failed authentication attempt.");
    }
}
