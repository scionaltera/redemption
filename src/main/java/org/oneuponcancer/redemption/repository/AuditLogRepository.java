package org.oneuponcancer.redemption.repository;

import org.oneuponcancer.redemption.model.AuditLog;
import org.oneuponcancer.redemption.model.AwardIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, AwardIdentity> {
}
