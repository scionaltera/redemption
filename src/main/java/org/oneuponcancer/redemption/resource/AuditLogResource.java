package org.oneuponcancer.redemption.resource;

import org.oneuponcancer.redemption.exception.InsufficientPermissionException;
import org.oneuponcancer.redemption.model.AuditLog;
import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/api/v1/audit")
public class AuditLogResource {
    private static final Sort SORT_DESC = new Sort(Sort.Direction.DESC,"timestamp");

    private AuditLogRepository auditLogRepository;
    private Integer auditServiceRequestMax;

    @Inject
    public AuditLogResource(Integer auditServiceRequestMax, AuditLogRepository auditLogRepository) {
        this.auditServiceRequestMax = auditServiceRequestMax;
        this.auditLogRepository = auditLogRepository;
    }

    @RequestMapping("")
    @ResponseBody
    public List<AuditLog> fetchAuditLogs(Principal principal, @RequestParam(required = false) Integer count) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.READ_LOGS.name()))) {
            throw new InsufficientPermissionException("Not allowed to read audit logs.");
        }

        if (count == null) {
            count = auditServiceRequestMax;
        } else if (count > auditServiceRequestMax) {
            throw new IllegalArgumentException("Only " + auditServiceRequestMax + " results are allowed per request.");
        } else if (count < 1) {
            throw new IllegalArgumentException("The number of returned results must be at least one.");
        }

        PageRequest request = new PageRequest(0, count, SORT_DESC);
        Page<AuditLog> results = auditLogRepository.findAll(request);

        return results.getContent();
    }

    @ExceptionHandler(InsufficientPermissionException.class)
    public void handleInsufficientPermissionException(InsufficientPermissionException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgument(IllegalArgumentException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }
}
