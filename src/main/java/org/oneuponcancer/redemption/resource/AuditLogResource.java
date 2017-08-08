package org.oneuponcancer.redemption.resource;

import org.oneuponcancer.redemption.model.AuditLog;
import org.oneuponcancer.redemption.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
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
    public List<AuditLog> fetchAuditLogs(@RequestParam(required = false) Integer count) {
        if (count == null) {
            count = auditServiceRequestMax;
        } else if (count > auditServiceRequestMax) {
            count = auditServiceRequestMax;
        } else if (count < 1) {
            count = 1;
        }

        PageRequest request = new PageRequest(0, count, SORT_DESC);
        Page<AuditLog> results = auditLogRepository.findAll(request);

        return results.getContent();
    }
}
